import { Beam } from './beam.js';

export class LightSource {
  constructor({ type = 'uniform', wavelength = 632.8e-9, waist = 0.001, bitmapData = null, width = 128, height = 128, physicalWidth = 0.01, physicalHeight = 0.01, xWaveNumber = 0, yWaveNumber = 0, mIndex = 1, nIndex = 1, topologicalCharge = 1, radialIndex = 0, radialWaveNumber = 1800, xCentre = 0, yCentre = 0 } = {}) {
    this.type = type;
    this.wavelength = wavelength;
    this.waist = waist;
    this.bitmapData = bitmapData;
    this.width = width;
    this.height = height;
    this.physicalWidth = physicalWidth;
    this.physicalHeight = physicalHeight;
    this.xWaveNumber = xWaveNumber;
    this.yWaveNumber = yWaveNumber;
    this.mIndex = mIndex;
    this.nIndex = nIndex;
    this.topologicalCharge = topologicalCharge;
    this.radialIndex = radialIndex;
    this.radialWaveNumber = radialWaveNumber;
    this.xCentre = xCentre;
    this.yCentre = yCentre;
  }

  output(options = {}) {
    const beam = new Beam(this.width ?? options.width, this.height ?? options.height, this.physicalWidth ?? options.physicalWidth, this.physicalHeight ?? options.physicalHeight, this.wavelength);
    if (this.type === 'gaussian') return beam.makeGaussian(this.waist, this.xCentre, this.yCentre, this.xWaveNumber, this.yWaveNumber);
    if (this.type === 'hermite-gaussian') return beam.makeHermiteGaussian(this.mIndex, this.nIndex, this.waist, this.xCentre, this.yCentre);
    if (this.type === 'laguerre-gaussian') return beam.makeLaguerreGaussian(this.topologicalCharge, this.radialIndex, this.waist, this.xCentre, this.yCentre);
    if (this.type === 'bessel') return beam.makeBessel(this.topologicalCharge, this.radialWaveNumber, this.xCentre, this.yCentre);
    if (this.type === 'bitmap' && this.bitmapData) {
      if (this.bitmapData.length === beam.real.length) return beam.makeBitmap(this.bitmapData);
      const sourceWidth = Math.round(Math.sqrt(this.bitmapData.length));
      const sourceHeight = Math.max(1, Math.floor(this.bitmapData.length / sourceWidth));
      const resized = new Float64Array(beam.real.length);
      for (let y = 0; y < beam.height; y++) for (let x = 0; x < beam.width; x++) {
        const sourceX = Math.min(sourceWidth - 1, Math.floor(x / beam.width * sourceWidth));
        const sourceY = Math.min(sourceHeight - 1, Math.floor(y / beam.height * sourceHeight));
        resized[y * beam.width + x] = this.bitmapData[sourceY * sourceWidth + sourceX];
      }
      return beam.makeBitmap(resized);
    }
    return beam.makeUniformPlaneWave(this.xWaveNumber, this.yWaveNumber);
  }
}

export class SlitAperture {
  constructor({ width = 2.5e-4, rotation = 0, xCentre = 0, yCentre = 0 } = {}) { this.width = width; this.rotation = rotation; this.xCentre = xCentre; this.yCentre = yCentre; }
  apply(beam) {
    const cosine = Math.cos(this.rotation);
    const sine = Math.sin(this.rotation);
    return beam.multiplyTransmission((x, y) => {
      const localX = (x - this.xCentre) * cosine + (y - this.yCentre) * sine;
      return Math.abs(localX) <= this.width / 2 ? 1 : 0;
    });
  }
}

export class PolygonalAperture {
  constructor({ sides = 6, radius = 1e-3, rotation = 0, xCentre = 0, yCentre = 0 } = {}) { this.sides = Math.max(3, Math.round(sides)); this.radius = radius; this.rotation = rotation; this.xCentre = xCentre; this.yCentre = yCentre; }
  apply(beam) {
    return beam.multiplyTransmission((x, y) => {
      const localX = x - this.xCentre;
      const localY = y - this.yCentre;
      for (let side = 0; side < this.sides; side++) {
        const angle = this.rotation + side * 2 * Math.PI / this.sides;
        if (localX * Math.cos(angle) + localY * Math.sin(angle) > this.radius) return 0;
      }
      return 1;
    });
  }
}

export class AbsorbingBoundary {
  constructor(width = 10) { this.width = width; }
  apply(beam) { return beam.applyAbsorbingBoundary(this.width); }
}

export class ApertureStack {
  constructor(aperture, count = 10, separation = 1e-3) {
    this.aperture = aperture;
    this.count = Math.max(1, Math.round(count));
    this.separation = separation;
  }
  apply(beam) {
    this.aperture.apply(beam);
    for (let index = 1; index < this.count; index++) {
      beam.propagate(this.separation);
      this.aperture.apply(beam);
    }
    return beam;
  }
}

export class GaussianAperture {
  constructor({ sigma = 1e-3, xCentre = 0, yCentre = 0 } = {}) { this.sigma = sigma; this.xCentre = xCentre; this.yCentre = yCentre; }
  apply(beam) { return beam.multiplyTransmission((x, y) => Math.exp(-((x - this.xCentre) ** 2 + (y - this.yCentre) ** 2) / (2 * this.sigma ** 2))); }
}

export class AnnularAperture {
  constructor({ outerRadius = 1e-3, innerRadius = 0.2e-3, xCentre = 0, yCentre = 0 } = {}) { this.outerRadius = outerRadius; this.innerRadius = innerRadius; this.xCentre = xCentre; this.yCentre = yCentre; }
  apply(beam) {
    return beam.multiplyTransmission((x, y) => {
      const radius = Math.hypot(x - this.xCentre, y - this.yCentre);
      return radius <= this.outerRadius && radius >= this.innerRadius ? 1 : 0;
    });
  }
}

export class DoubleSlitAperture {
  constructor({ separation = 1e-3, width = 2.5e-4, rotation = 0, xCentre = 0, yCentre = 0 } = {}) {
    this.separation = separation;
    this.width = width;
    this.rotation = rotation;
    this.xCentre = xCentre;
    this.yCentre = yCentre;
  }

  apply(beam) {
    const cosine = Math.cos(this.rotation);
    const sine = Math.sin(this.rotation);
    return beam.multiplyTransmission((x, y) => {
      const localX = (x - this.xCentre) * cosine + (y - this.yCentre) * sine;
      const halfWidth = this.width / 2;
      return Math.abs(localX - this.separation / 2) <= halfWidth || Math.abs(localX + this.separation / 2) <= halfWidth ? 1 : 0;
    });
  }
}

export class SingleSlitAperture {
  constructor({ width = 2.5e-4, rotation = 0, xCentre = 0, yCentre = 0 } = {}) {
    this.width = width;
    this.rotation = rotation;
    this.xCentre = xCentre;
    this.yCentre = yCentre;
  }

  apply(beam) {
    const cosine = Math.cos(this.rotation);
    const sine = Math.sin(this.rotation);
    return beam.multiplyTransmission((x, y) => {
      const localX = (x - this.xCentre) * cosine + (y - this.yCentre) * sine;
      return Math.abs(localX) <= this.width / 2 ? 1 : 0;
    });
  }
}

export class GratingAperture {
  constructor({ period = 8e-4, width = 1.5e-4 } = {}) {
    this.period = period;
    this.width = width;
  }

  apply(beam) {
    return beam.multiplyTransmission((x) => {
      const localCoordinate = ((x + this.period / 2) % this.period + this.period) % this.period - this.period / 2;
      return Math.abs(localCoordinate) <= this.width / 2 ? 1 : 0;
    });
  }
}

export class Lens {
  constructor(focalLength = 0.6, xCentre = 0, yCentre = 0) {
    this.focalLength = focalLength;
    this.xCentre = xCentre;
    this.yCentre = yCentre;
  }

  apply(beam) {
    const waveNumber = 2 * Math.PI / beam.wavelength;
    return beam.applyPhase((x, y) => -waveNumber * ((x - this.xCentre) ** 2 + (y - this.yCentre) ** 2) / (2 * this.focalLength));
  }
}

export class Distance {
  constructor(distance = 0.6) { this.distance = distance; }
  apply(beam) { return beam.propagate(this.distance); }
}

export class Plane {
  constructor(name = 'Plane') { this.name = name; this.beam = null; }
  apply(beam) { this.beam = beam.clone(); return beam; }
  snapshot() { return this.beam?.clone() ?? null; }
}

export class Hologram {
  constructor(name = 'Hologram') { this.name = name; }

  apply(inputs) {
    const hologramInput = inputs[0];
    const incidentInput = inputs[1];
    if (!hologramInput && !incidentInput) return null;
    const hologram = hologramInput ?? Beam.darkLike(incidentInput);
    const incident = incidentInput ?? Beam.darkLike(hologram);
    if (hologram.width !== incident.width || hologram.height !== incident.height ||
      hologram.physicalWidth !== incident.physicalWidth || hologram.physicalHeight !== incident.physicalHeight ||
      hologram.wavelength !== incident.wavelength) {
      throw new Error(`Hologram inputs to ${this.name} are incompatible`);
    }
    const output = hologram.clone();
    for (let index = 0; index < output.real.length; index++) {
      const hologramReal = output.real[index];
      const hologramImaginary = output.imaginary[index];
      output.real[index] = hologramReal * incident.real[index] - hologramImaginary * incident.imaginary[index];
      output.imaginary[index] = hologramReal * incident.imaginary[index] + hologramImaginary * incident.real[index];
    }
    return output;
  }
}

export class Hologrammifier {
  constructor(type = 'phase', phaseStepHeightFactor = 1) { this.type = type; this.phaseStepHeightFactor = phaseStepHeightFactor; }
  apply(beam) {
    for (let index = 0; index < beam.real.length; index++) {
      if (this.type === 'intensity') {
        beam.real[index] = beam.real[index] ** 2 + beam.imaginary[index] ** 2;
        beam.imaginary[index] = 0;
      } else {
        const phase = Math.atan2(beam.imaginary[index], beam.real[index]) * this.phaseStepHeightFactor;
        beam.real[index] = Math.cos(phase);
        beam.imaginary[index] = Math.sin(phase);
      }
    }
    return beam;
  }
}

export class HologramFromBitmap {
  constructor(bitmapData = null) { this.bitmapData = bitmapData; }
  apply(beam) {
    if (!this.bitmapData) return beam;
    const sourcePixels = this.bitmapData.length / 2;
    const sourceWidth = Math.round(Math.sqrt(sourcePixels));
    const sourceHeight = Math.max(1, Math.floor(sourcePixels / sourceWidth));
    for (let y = 0; y < beam.height; y++) for (let x = 0; x < beam.width; x++) {
      const sourceX = Math.min(sourceWidth - 1, Math.floor(x / beam.width * sourceWidth));
      const sourceY = Math.min(sourceHeight - 1, Math.floor(y / beam.height * sourceHeight));
      const source = (sourceY * sourceWidth + sourceX) * 2;
      const index = beam.index(x, y);
      const real = beam.real[index];
      const imaginary = beam.imaginary[index];
      const hologramReal = this.bitmapData[source];
      const hologramImaginary = this.bitmapData[source + 1];
      beam.real[index] = real * hologramReal - imaginary * hologramImaginary;
      beam.imaginary[index] = real * hologramImaginary + imaginary * hologramReal;
    }
    return beam;
  }
}

export class EitherOrComponent {
  constructor(maskData = null, threshold = 0.5) { this.maskData = maskData; this.threshold = threshold; }
  apply(beam) {
    if (!this.maskData) return beam;
    const sourceWidth = Math.round(Math.sqrt(this.maskData.length));
    const sourceHeight = Math.max(1, Math.floor(this.maskData.length / sourceWidth));
    for (let y = 0; y < beam.height; y++) for (let x = 0; x < beam.width; x++) {
      const sourceX = Math.min(sourceWidth - 1, Math.floor(x / beam.width * sourceWidth));
      const sourceY = Math.min(sourceHeight - 1, Math.floor(y / beam.height * sourceHeight));
      const mask = this.maskData[(sourceY * sourceWidth + sourceX)];
      const index = beam.index(x, y);
      if (mask > this.threshold) beam.imaginary[index] *= -1;
    }
    return beam;
  }
}

export class EitherOrPhaseConjugateSurface {
  constructor(maskData = null, threshold = 0.5) { this.maskData = maskData; this.threshold = threshold; }
  apply(beam) {
    if (!this.maskData) return beam;
    const sourceWidth = Math.round(Math.sqrt(this.maskData.length));
    const sourceHeight = Math.max(1, Math.floor(this.maskData.length / sourceWidth));
    for (let y = 0; y < beam.height; y++) for (let x = 0; x < beam.width; x++) {
      const sourceX = Math.min(sourceWidth - 1, Math.floor(x / beam.width * sourceWidth));
      const sourceY = Math.min(sourceHeight - 1, Math.floor(y / beam.height * sourceHeight));
      if (this.maskData[sourceY * sourceWidth + sourceX] > this.threshold) beam.phaseConjugateAt(x, y);
    }
    return beam;
  }
}

export class BeamSplitter {
  constructor(name = 'Beam splitter') { this.name = name; }

  apply(inputs) {
    const first = inputs[0] ?? Beam.darkLike(inputs[1]);
    const second = inputs[1] ?? Beam.darkLike(first);
    return [
      first.clone().add(second).scale(1 / Math.sqrt(2)),
      first.clone().subtract(second).scale(1 / Math.sqrt(2))
    ];
  }
}

export class Mirror {
  constructor(reflectionCoefficient = 0.99) {
    this.reflectionCoefficient = reflectionCoefficient;
    this.transmissionCoefficient = Math.sqrt(Math.max(0, 1 - reflectionCoefficient ** 2));
  }

  apply(inputs) {
    const ports = Array.isArray(inputs) ? inputs : [inputs];
    const first = ports[0] ?? Beam.darkLike(ports[1]);
    const second = ports[1] ?? Beam.darkLike(first);
    const reflectedFirst = first.clone().scale(this.reflectionCoefficient);
    const transmittedSecond = second.clone().scale(this.transmissionCoefficient).applyPhase(() => Math.PI / 2);
    const reflectedSecond = second.clone().scale(this.reflectionCoefficient);
    const transmittedFirst = first.clone().scale(this.transmissionCoefficient).applyPhase(() => Math.PI / 2);
    return [
      reflectedFirst.add(transmittedSecond),
      reflectedSecond.add(transmittedFirst)
    ];
  }
}

export class NeutralDensityFilter {
  constructor({ type = 'factor', value = 1 } = {}) { this.type = type; this.value = value; }
  apply(beam) {
    let amplitudeFactor = 1;
    if (this.type === 'factor') amplitudeFactor = Math.sqrt(Math.max(0, this.value));
    else if (this.type === 'optical-density') amplitudeFactor = 10 ** (-0.5 * this.value);
    else if (this.type === 'maximum-intensity') amplitudeFactor = Math.sqrt(this.value / Math.max(beam.maxIntensity(), 1e-30));
    else if (this.type === 'power') amplitudeFactor = Math.sqrt(this.value / Math.max(beam.totalPower(), 1e-30));
    return beam.scale(amplitudeFactor);
  }
}

export class SpiralPhasePlate {
  constructor(topologicalCharge = 1) { this.topologicalCharge = topologicalCharge; }
  apply(beam) { return beam.applyPhase((x, y) => this.topologicalCharge * Math.atan2(y, x)); }
}

export class TransparentComponent {
  apply(beam) { return beam; }
}

export class CloneOfComponent {
  constructor(delegate = null) { this.delegate = delegate; }
  apply(beam) { return this.delegate ? this.delegate.apply(beam) : beam; }
}

export class BeamExpander {
  constructor(magnification = 1) { this.magnification = magnification; }
  apply(beam) {
    const magnitude = Math.max(Math.abs(this.magnification), 1e-12);
    beam.setPhysicalDimensions(beam.physicalWidth * magnitude, beam.physicalHeight * magnitude).scale(1 / magnitude);
    return this.magnification < 0 ? beam.flip() : beam;
  }
}

export class BeamTransformer {
  constructor(rotationAngle = 0, zoomFactor = 1, clip = true) { this.rotationAngle = rotationAngle; this.zoomFactor = zoomFactor; this.clip = clip; }
  apply(beam) { return beam.rotateAndZoom(this.rotationAngle, this.zoomFactor, this.clip); }
}

export class DovePrism {
  constructor(rotationAngle = 0) { this.rotationAngle = rotationAngle; }
  apply(beam) {
    beam.rotateAndZoom(-this.rotationAngle, 1, true);
    beam.flipHorizontal();
    return beam.rotateAndZoom(this.rotationAngle, 1, true);
  }
}

export class DovePrismArray {
  constructor(prismWidth = 8, rotationAngle = 0) { this.prismWidth = prismWidth; this.rotationAngle = rotationAngle; }
  apply(beam) {
    beam.rotateAndZoom(-this.rotationAngle, 1, true);
    beam.flipHorizontalBlocks(this.prismWidth);
    return beam.rotateAndZoom(this.rotationAngle, 1, true);
  }
}

export class FourierLens {
  constructor(focalLength = 1, keepPhysicalDimensions = false) { this.focalLength = focalLength; this.keepPhysicalDimensions = keepPhysicalDimensions; }
  apply(beam) {
    const oldWidth = beam.physicalWidth;
    const oldHeight = beam.physicalHeight;
    beam.fourierTransform(true);
    if (!this.keepPhysicalDimensions) {
      beam.setPhysicalDimensions(this.focalLength * beam.wavelength * beam.width / oldWidth, this.focalLength * beam.wavelength * beam.height / oldHeight);
    }
    return beam;
  }
}

export class CylindricalLens {
  constructor(focalLength = 1, axisAngle = 0) { this.focalLength = focalLength; this.axisAngle = axisAngle; }
  apply(beam) {
    const sine = Math.sin(this.axisAngle);
    const cosine = Math.cos(this.axisAngle);
    const waveNumber = 2 * Math.PI / beam.wavelength;
    return beam.applyPhase((x, y) => {
      const distanceFromAxis = x * cosine - y * sine;
      return -waveNumber * distanceFromAxis ** 2 / (2 * this.focalLength);
    });
  }
}

export class CylindricalLensModeConverter {
  constructor(designWaistSize = 1e-3, designWavelength = 632.8e-9, axisAngle = Math.PI / 4) {
    this.designWaistSize = designWaistSize;
    this.designWavelength = designWavelength;
    this.axisAngle = axisAngle;
  }
  apply(beam) {
    const focalLength = Math.PI * this.designWaistSize ** 2 / this.designWavelength / (1 + 1 / Math.SQRT2);
    const separation = Math.SQRT2 * focalLength;
    beam.propagate(-separation / 2);
    new CylindricalLens(focalLength, -this.axisAngle).apply(beam);
    beam.propagate(separation);
    new CylindricalLens(focalLength, -this.axisAngle).apply(beam);
    return beam.propagate(-separation / 2);
  }
}

export class SpiralAdaptiveFresnelLensComponent {
  constructor({ type = 'logarithmic', p = 1 / (10 * Math.PI / 180), b = 0.1, phi0 = 0, boundary = 'half-way', azimuthalCompensation = true } = {}) {
    this.type = type;
    this.p = p;
    this.b = b;
    this.phi0 = phi0;
    this.boundary = boundary;
    this.azimuthalCompensation = azimuthalCompensation;
  }
  windingNumber(radius, angle) {
    const rotated = angle - this.phi0;
    const b2pi = 2 * Math.PI * this.b;
    if (this.type === 'archimedean') return Math.floor(0.5 + (radius - this.b * rotated) / b2pi);
    if (this.type === 'fermat') return Math.floor(0.5 + (this.b * Math.PI ** 2 + radius ** 4 - 2 * this.b * radius ** 2 * rotated) / (4 * this.b * Math.PI * radius ** 2));
    if (this.type === 'hyperbolic') return Math.floor(0.5 - (this.b * (rotated + Math.sqrt(Math.PI ** 2 + 1 / (2 * this.b * radius) ** 2)) + 1 / (2 * radius)) / b2pi);
    if (this.boundary === 'rotated-spiral') return Math.floor(0.5 + (Math.log(radius) - this.b * rotated) / b2pi);
    return Math.floor(0.5 + (Math.log(2 * radius / (Math.exp(-b2pi) + 1)) - this.b * (rotated + Math.PI)) / b2pi);
  }
  apply(beam) {
    const focalScale = this.b / this.p;
    const waveNumber = 2 * Math.PI / beam.wavelength;
    return beam.applyPhase((x, y) => {
      const radius = Math.max(Math.hypot(x, y), 1e-12);
      const angle = Math.atan2(y, x);
      const winding = this.windingNumber(radius, angle);
      const psi = angle + winding * 2 * Math.PI - this.phi0;
      let radialCentre;
      let focalLength;
      if (this.type === 'archimedean') { radialCentre = this.b * psi; focalLength = focalScale / Math.max(Math.abs(radialCentre), 1e-12); }
      else if (this.type === 'fermat') { radialCentre = Math.sqrt(Math.max(0, 2 * this.b * psi)); focalLength = focalScale / Math.max(radialCentre ** 2, 1e-12); }
      else if (this.type === 'hyperbolic') { radialCentre = -1 / (this.b * psi || 1e-12); focalLength = focalScale * radialCentre; }
      else { radialCentre = Math.exp(this.b * psi); focalLength = focalScale; }
      let phase = -waveNumber * (radius - radialCentre) ** 2 / (2 * focalLength);
      if (this.azimuthalCompensation) {
        if (this.type === 'archimedean') phase += radialCentre ** 2 * waveNumber / (6 * focalLength);
        else if (this.type === 'fermat') phase += radialCentre ** 2 * waveNumber / (8 * focalLength);
        else if (this.type === 'hyperbolic') phase += waveNumber * radialCentre / (2 * focalScale);
        else phase += radialCentre ** 2 * waveNumber / (4 * focalScale);
      }
      return phase;
    });
  }
}

export class Wedge {
  constructor(deflectionAngleXZ = 0, deflectionAngleYZ = 0) { this.deflectionAngleXZ = deflectionAngleXZ; this.deflectionAngleYZ = deflectionAngleYZ; }
  apply(beam) {
    const waveNumber = 2 * Math.PI / beam.wavelength;
    const xSlope = Math.sin(-this.deflectionAngleXZ);
    const ySlope = Math.sin(-this.deflectionAngleYZ);
    return beam.applyPhase((x, y) => -waveNumber * (x * xSlope + y * ySlope));
  }
}

export class PhaseConjugator {
  apply(beam) { return beam.phaseConjugate(); }
}

export class LensletArray {
  constructor(arrayPeriod = 1e-3, focalLength = 1, rotation = 0) { this.arrayPeriod = arrayPeriod; this.focalLength = focalLength; this.rotation = rotation; }
  apply(beam) {
    const sine = Math.sin(this.rotation);
    const cosine = Math.cos(this.rotation);
    const waveNumber = 2 * Math.PI / beam.wavelength;
    return beam.applyPhase((x, y) => {
      const rotatedX = x * cosine + y * sine;
      const rotatedY = y * cosine - x * sine;
      const localX = rotatedX - this.arrayPeriod * Math.floor(rotatedX / this.arrayPeriod + 0.5);
      const localY = rotatedY - this.arrayPeriod * Math.floor(rotatedY / this.arrayPeriod + 0.5);
      return -waveNumber * (localX ** 2 + localY ** 2) / (2 * this.focalLength);
    });
  }
}

export class OpticalGraph {
  constructor() {
    this.nodes = new Map();
    this.connections = new Map();
  }

  addNode(id, component, { inputs = 1, outputs = 1 } = {}) {
    if (this.nodes.has(id)) throw new Error(`Duplicate optical node: ${id}`);
    this.nodes.set(id, { id, component, inputs, outputs });
    return this;
  }

  connect(sourceId, sourceOutput, targetId, targetInput) {
    const source = this.nodes.get(sourceId);
    const target = this.nodes.get(targetId);
    if (!source || !target) throw new Error('Optical graph connection references an unknown node');
    if (sourceOutput < 0 || sourceOutput >= source.outputs || targetInput < 0 || targetInput >= target.inputs) throw new RangeError('Optical graph port index is out of range');
    this.connections.set(`${targetId}:${targetInput}`, { sourceId, sourceOutput });
    return this;
  }

  run() {
    const cache = new Map();
    const visiting = new Set();
    const evaluate = (id) => {
      if (cache.has(id)) return cache.get(id);
      if (visiting.has(id)) throw new Error(`Optical graph cycle detected at ${id}`);
      const node = this.nodes.get(id);
      if (!node) throw new Error(`Unknown optical node: ${id}`);
      visiting.add(id);
      let outputs;
      if (node.component instanceof LightSource) {
        outputs = [node.component.output({ width: 128, height: 128, physicalWidth: 0.01, physicalHeight: 0.01 })];
      } else {
        const inputs = Array.from({ length: node.inputs }, (_, input) => {
          const connection = this.connections.get(`${id}:${input}`);
          return connection ? evaluate(connection.sourceId)[connection.sourceOutput] : null;
        });
        const result = typeof node.component.apply === 'function' ? node.component.apply(inputs.length === 1 ? inputs[0] : inputs) : null;
        outputs = Array.isArray(result) ? result : [result];
      }
      visiting.delete(id);
      cache.set(id, outputs);
      return outputs;
    };
    for (const id of this.nodes.keys()) evaluate(id);
    return { outputs: cache, planes: [...this.nodes.values()].filter(node => node.component instanceof Plane).map(node => node.component) };
  }
}

export class OpticalSystem {
  constructor(components = []) { this.components = components; this.planes = []; }
  run() {
    this.planes = [];
    const source = this.components.find(component => component instanceof LightSource);
    if (!source) throw new Error('An optical system needs a light source');
    let beam = source.output({ width: 128, height: 128, physicalWidth: 0.01, physicalHeight: 0.01 });
    for (const component of this.components.slice(1)) {
      const result = component.apply(beam);
      beam = Array.isArray(result) ? result[0] : result;
      if (component instanceof Plane) this.planes.push(component);
    }
    return beam;
  }
}