import { fft2d } from './fft.js';

export class Beam {
  constructor(width = 128, height = 128, physicalWidth = 0.01, physicalHeight = 0.01, wavelength = 632.8e-9) {
    if ((width & (width - 1)) !== 0 || (height & (height - 1)) !== 0) throw new RangeError('Beam dimensions must be powers of two');
    this.width = width;
    this.height = height;
    this.physicalWidth = physicalWidth;
    this.physicalHeight = physicalHeight;
    this.wavelength = wavelength;
    // create space for data, and initialise with zeroes
    this.real = new Float64Array(width * height);
    this.imaginary = new Float64Array(width * height);
  }

  clone() {
    const copy = new Beam(this.width, this.height, this.physicalWidth, this.physicalHeight, this.wavelength);
    copy.real.set(this.real);
    copy.imaginary.set(this.imaginary);
    return copy;
  }

  static darkLike(beam) { return new Beam(beam.width, beam.height, beam.physicalWidth, beam.physicalHeight, beam.wavelength); }

  add(other) {
    for (let index = 0; index < this.real.length; index++) {
      this.real[index] += other.real[index];
      this.imaginary[index] += other.imaginary[index];
    }
    return this;
  }

  subtract(other) {
    for (let index = 0; index < this.real.length; index++) {
      this.real[index] -= other.real[index];
      this.imaginary[index] -= other.imaginary[index];
    }
    return this;
  }

  scale(factor) {
    for (let index = 0; index < this.real.length; index++) {
      this.real[index] *= factor;
      this.imaginary[index] *= factor;
    }
    return this;
  }

  phaseConjugate() {
    for (let index = 0; index < this.imaginary.length; index++) this.imaginary[index] *= -1;
    return this;
  }

  phaseConjugateAt(x, y) {
    this.imaginary[this.index(x, y)] *= -1;
    return this;
  }

  totalPower() {
    let sum = 0;
    for (let index = 0; index < this.real.length; index++) sum += this.real[index] ** 2 + this.imaginary[index] ** 2;
    return sum * (this.physicalWidth / this.width) * (this.physicalHeight / this.height);
  }

  setPhysicalDimensions(width, height) {
    this.physicalWidth = width;
    this.physicalHeight = height;
    return this;
  }

  flip() {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width / 2; x++) {
      const left = this.index(x, y);
      const right = this.index(this.width - 1 - x, y);
      [this.real[left], this.real[right]] = [this.real[right], this.real[left]];
      [this.imaginary[left], this.imaginary[right]] = [this.imaginary[right], this.imaginary[left]];
    }
    for (let y = 0; y < this.height / 2; y++) for (let x = 0; x < this.width; x++) {
      const top = this.index(x, y);
      const bottom = this.index(x, this.height - 1 - y);
      [this.real[top], this.real[bottom]] = [this.real[bottom], this.real[top]];
      [this.imaginary[top], this.imaginary[bottom]] = [this.imaginary[bottom], this.imaginary[top]];
    }
    return this;
  }

  flipHorizontal() {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width / 2; x++) {
      const left = this.index(x, y);
      const right = this.index(this.width - 1 - x, y);
      [this.real[left], this.real[right]] = [this.real[right], this.real[left]];
      [this.imaginary[left], this.imaginary[right]] = [this.imaginary[right], this.imaginary[left]];
    }
    return this;
  }

  flipHorizontalBlocks(blockWidth) {
    if (!Number.isInteger(blockWidth) || blockWidth < 1 || (blockWidth & (blockWidth - 1)) !== 0 || blockWidth > this.width) return this;
    const nextReal = new Float64Array(this.real.length);
    const nextImaginary = new Float64Array(this.imaginary.length);
    const halfBlock = blockWidth / 2;
    for (let x = 0; x < this.width; x++) {
      const sourceX = Math.floor((x + halfBlock) / blockWidth) * blockWidth - ((x + halfBlock) % blockWidth) + halfBlock - 1;
      for (let y = 0; y < this.height; y++) {
        const target = this.index(x, y);
        if (sourceX >= 0 && sourceX < this.width) {
          const source = this.index(sourceX, y);
          nextReal[target] = this.real[source];
          nextImaginary[target] = this.imaginary[source];
        }
      }
    }
    this.real = nextReal;
    this.imaginary = nextImaginary;
    return this;
  }

  swapQuadrants() {
    const nextReal = new Float64Array(this.real.length);
    const nextImaginary = new Float64Array(this.imaginary.length);
    const halfWidth = this.width / 2;
    const halfHeight = this.height / 2;
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const sourceX = (x + halfWidth) % this.width;
      const sourceY = (y + halfHeight) % this.height;
      const target = this.index(x, y);
      const source = this.index(sourceX, sourceY);
      nextReal[target] = this.real[source];
      nextImaginary[target] = this.imaginary[source];
    }
    this.real = nextReal;
    this.imaginary = nextImaginary;
    return this;
  }

  rotateAndZoom(angle, zoomFactor = 1, clipping = true) {
    const oldWidth = this.width;
    const oldHeight = this.height;
    const newWidth = clipping ? oldWidth : 2 * Math.max(oldWidth, oldHeight);
    const newHeight = clipping ? oldHeight : 2 * Math.max(oldWidth, oldHeight);
    const magnitude = Math.max(Math.abs(zoomFactor), 1e-12);
    const adjustedAngle = angle + (zoomFactor < 0 ? Math.PI : 0);
    const cosine = Math.cos(adjustedAngle) / magnitude;
    const sine = Math.sin(adjustedAngle) / magnitude;
    const oldReal = this.real;
    const oldImaginary = this.imaginary;
    const nextReal = new Float64Array(newWidth * newHeight);
    const nextImaginary = new Float64Array(newWidth * newHeight);
    const sourceCentreX = (oldWidth - 1) / 2;
    const sourceCentreY = (oldHeight - 1) / 2;
    const targetCentreX = (newWidth - 1) / 2;
    const targetCentreY = (newHeight - 1) / 2;
    const sourceIndex = (x, y) => y * oldWidth + x;
    const sample = (values, x, y) => {
      if (x < 0 || y < 0 || x >= oldWidth - 1 || y >= oldHeight - 1) return 0;
      const left = Math.floor(x);
      const top = Math.floor(y);
      const xFraction = x - left;
      const yFraction = y - top;
      const topLeft = values[sourceIndex(left, top)];
      const topRight = values[sourceIndex(left + 1, top)];
      const bottomLeft = values[sourceIndex(left, top + 1)];
      const bottomRight = values[sourceIndex(left + 1, top + 1)];
      return topLeft * (1 - xFraction) * (1 - yFraction) + topRight * xFraction * (1 - yFraction) + bottomLeft * (1 - xFraction) * yFraction + bottomRight * xFraction * yFraction;
    };
    for (let y = 0; y < newHeight; y++) for (let x = 0; x < newWidth; x++) {
      const relativeX = x - targetCentreX;
      const relativeY = y - targetCentreY;
      const sourceX = sourceCentreX + cosine * relativeX + sine * relativeY;
      const sourceY = sourceCentreY - sine * relativeX + cosine * relativeY;
      const target = y * newWidth + x;
      nextReal[target] = sample(oldReal, sourceX, sourceY);
      nextImaginary[target] = sample(oldImaginary, sourceX, sourceY);
    }
    this.width = newWidth;
    this.height = newHeight;
    this.real = nextReal;
    this.imaginary = nextImaginary;
    return this;
  }

  fourierTransform(inverse = false) {
    fft2d(this.real, this.imaginary, this.width, this.height, inverse);
    return this.scale(1 / Math.sqrt(this.width * this.height));
  }

  index(x, y) { return y * this.width + x; }
  xCoordinate(x) { return this.physicalWidth / this.width * (x - (this.width - 1) / 2); }
  yCoordinate(y) { return this.physicalHeight / this.height * (y - (this.height - 1) / 2); }
  intensityAt(x, y) { const index = this.index(x, y); return this.real[index] ** 2 + this.imaginary[index] ** 2; }

  maxIntensity() {
    let maximum = 0;
    for (let index = 0; index < this.real.length; index++) maximum = Math.max(maximum, this.real[index] ** 2 + this.imaginary[index] ** 2);
    return maximum;
  }

  intensityRow(y = Math.floor(this.height / 2)) {
    const row = new Float64Array(this.width);
    for (let x = 0; x < this.width; x++) row[x] = this.intensityAt(x, y);
    return row;
  }

  amplitudeRow(y = Math.floor(this.height / 2)) {
    const row = new Float64Array(this.width);
    for (let x = 0; x < this.width; x++) {
      const index = this.index(x, y);
      row[x] = Math.hypot(this.real[index], this.imaginary[index]);
    }
    return row;
  }

  realPartRow(y = Math.floor(this.height / 2)) {
    const row = new Float64Array(this.width);
    for (let x = 0; x < this.width; x++) {
      const index = this.index(x, y);
      row[x] = this.real[index];
    }
    return row;
  }

  imaginaryPartRow(y = Math.floor(this.height / 2)) {
    const row = new Float64Array(this.width);
    for (let x = 0; x < this.width; x++) {
      const index = this.index(x, y);
      row[x] = this.imaginary[index];
    }
    return row;
  }

  phaseRow(y = Math.floor(this.height / 2)) {
    const row = new Float64Array(this.width);
    for (let x = 0; x < this.width; x++) row[x] = Math.atan2(this.imaginary[this.index(x, y)], this.real[this.index(x, y)]);
    return row;
  }

  makeUniformPlaneWave(xWaveNumber = 0, yWaveNumber = 0) {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const phase = xWaveNumber * this.xCoordinate(x) + yWaveNumber * this.yCoordinate(y);
      const index = this.index(x, y);
      this.real[index] = Math.cos(phase);
      this.imaginary[index] = Math.sin(phase);
    }
    return this;
  }

  makeGaussian(waist, xCentre = 0, yCentre = 0, xWaveNumber = 0, yWaveNumber = 0) {
    const waistSquared = waist ** 2;
    for (let y = 0; y < this.height; y++) {
      const phaseY = yWaveNumber * this.yCoordinate(y);
      const distanceSquaredY = (this.yCoordinate(y) - yCentre) ** 2;
      for (let x = 0; x < this.width; x++) {
        const phase = xWaveNumber * this.xCoordinate(x) + phaseY;
        const distanceSquared = (this.xCoordinate(x) - xCentre) ** 2 + distanceSquaredY;
        const modAmplitude = Math.exp(-distanceSquared / waistSquared);
        const index = this.index(x, y);
        this.real[index] = Math.cos(phase) * modAmplitude;
        this.imaginary[index] = Math.sin(phase) * modAmplitude;
      }
    }
    return this;
  }

  makeHermiteGaussian(mIndex, nIndex, waist, xCentre = 0, yCentre = 0) {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const scaledX = Math.SQRT2 * (this.xCoordinate(x) - xCentre) / waist;
      const scaledY = Math.SQRT2 * (this.yCoordinate(y) - yCentre) / waist;
      const envelope = Math.exp(-(scaledX ** 2 + scaledY ** 2) / 2);
      this.real[this.index(x, y)] = hermite(mIndex, scaledX) * hermite(nIndex, scaledY) * envelope;
    }
    return this;
  }

  makeLaguerreGaussian(topologicalCharge, radialIndex, waist, xCentre = 0, yCentre = 0) {
    const azimuthalIndex = Math.abs(Math.round(topologicalCharge));
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const localX = this.xCoordinate(x) - xCentre;
      const localY = this.yCoordinate(y) - yCentre;
      const radiusSquared = localX ** 2 + localY ** 2;
      const scaledRadiusSquared = 2 * radiusSquared / waist ** 2;
      const amplitude = Math.pow(Math.sqrt(scaledRadiusSquared), azimuthalIndex) * generalizedLaguerre(radialIndex, azimuthalIndex, scaledRadiusSquared) * Math.exp(-scaledRadiusSquared / 2);
      const phase = topologicalCharge * Math.atan2(localY, localX);
      const index = this.index(x, y);
      this.real[index] = amplitude * Math.cos(phase);
      this.imaginary[index] = amplitude * Math.sin(phase);
    }
    return this;
  }

  makeBessel(topologicalCharge, radialWaveNumber, xCentre = 0, yCentre = 0) {
    const order = Math.abs(Math.round(topologicalCharge));
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const localX = this.xCoordinate(x) - xCentre;
      const localY = this.yCoordinate(y) - yCentre;
      const radius = Math.hypot(localX, localY);
      const phase = topologicalCharge * Math.atan2(localY, localX);
      const amplitude = besselFirstKind(order, radialWaveNumber * radius);
      const index = this.index(x, y);
      this.real[index] = amplitude * Math.cos(phase);
      this.imaginary[index] = amplitude * Math.sin(phase);
    }
    return this;
  }

  makeBitmap(pixels) {
    if (pixels.length !== this.real.length) throw new RangeError('Bitmap dimensions must match the beam dimensions');
    for (let index = 0; index < pixels.length; index++) this.real[index] = pixels[index];
    return this;
  }

  applyPhase(phaseFunction) {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const index = this.index(x, y);
      const phase = phaseFunction(this.xCoordinate(x), this.yCoordinate(y));
      const real = this.real[index];
      const imaginary = this.imaginary[index];
      this.real[index] = real * Math.cos(phase) - imaginary * Math.sin(phase);
      this.imaginary[index] = real * Math.sin(phase) + imaginary * Math.cos(phase);
    }
    return this;
  }

  multiplyTransmission(transmissionFunction) {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const factor = transmissionFunction(this.xCoordinate(x), this.yCoordinate(y));
      const index = this.index(x, y);
      this.real[index] *= factor;
      this.imaginary[index] *= factor;
    }
    return this;
  }

  applyAbsorbingBoundary(width) {
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const xFactor = x < width ? taper(x, width) : x >= this.width - width ? taper(this.width - 1 - x, width) : 1;
      const yFactor = y < width ? taper(y, width) : y >= this.height - width ? taper(this.height - 1 - y, width) : 1;
      const factor = xFactor * yFactor;
      const index = this.index(x, y);
      this.real[index] *= factor;
      this.imaginary[index] *= factor;
    }
    return this;
  }

  propagate(distance) {
    fft2d(this.real, this.imaginary, this.width, this.height);
    const waveNumber = 2 * Math.PI / this.wavelength;
    for (let y = 0; y < this.height; y++) for (let x = 0; x < this.width; x++) {
      const kx = 2 * Math.PI * (x <= this.width / 2 ? x : x - this.width) / this.physicalWidth;
      const ky = 2 * Math.PI * (y <= this.height / 2 ? y : y - this.height) / this.physicalHeight;
      const transverseSquared = kx ** 2 + ky ** 2;
      const kz = transverseSquared <= waveNumber ** 2 ? Math.sqrt(waveNumber ** 2 - transverseSquared) : 0;
      const decay = transverseSquared <= waveNumber ** 2 ? 1 : Math.exp(-Math.sqrt(transverseSquared - waveNumber ** 2) * Math.abs(distance));
      const phase = kz * distance;
      const factorReal = decay * Math.cos(phase);
      const factorImaginary = decay * Math.sin(phase);
      const index = this.index(x, y);
      const real = this.real[index];
      const imaginary = this.imaginary[index];
      this.real[index] = real * factorReal - imaginary * factorImaginary;
      this.imaginary[index] = real * factorImaginary + imaginary * factorReal;
    }
    fft2d(this.real, this.imaginary, this.width, this.height, true);
    return this;
  }
}

function taper(index, width) { return 0.5 - 0.5 * Math.cos(index / width * Math.PI); }

function hermite(order, value) {
  if (order === 0) return 1;
  if (order === 1) return 2 * value;
  let previous = 1;
  let current = 2 * value;
  for (let index = 2; index <= order; index++) [previous, current] = [current, 2 * value * current - 2 * (index - 1) * previous];
  return current;
}

function generalizedLaguerre(order, alpha, value) {
  if (order === 0) return 1;
  if (order === 1) return 1 + alpha - value;
  let previous = 1;
  let current = 1 + alpha - value;
  for (let index = 2; index <= order; index++) {
    const next = ((2 * index - 1 + alpha - value) * current - (index - 1 + alpha) * previous) / index;
    previous = current;
    current = next;
  }
  return current;
}

function besselFirstKind(order, value) {
  let sum = 0;
  for (let index = 0; index < 20; index++) {
    const numerator = (-1) ** index * (value / 2) ** (2 * index + order);
    let denominator = factorial(index) * factorial(index + order);
    sum += numerator / denominator;
    if (Math.abs(numerator / denominator) < 1e-14) break;
  }
  return sum;
}

function factorial(value) {
  let result = 1;
  for (let index = 2; index <= value; index++) result *= index;
  return result;
}