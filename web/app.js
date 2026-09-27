import {
  AbsorbingBoundary,
  BeamSplitter,
  BeamExpander,
  BeamTransformer,
  CylindricalLens,
  CylindricalLensModeConverter,
  CloneOfComponent,
  Distance,
  EitherOrComponent,
  EitherOrPhaseConjugateSurface,
  DovePrism,
  DovePrismArray,
  AnnularAperture,
  ApertureStack,
  DoubleSlitAperture,
  GaussianAperture,
  GratingAperture,
  Lens,
  LightSource,
  Mirror,
  FourierLens,
  LensletArray,
  NeutralDensityFilter,
  OpticalGraph,
  OpticalSystem,
  Hologram,
  HologramFromBitmap,
  Hologrammifier,
  PolygonalAperture,
  Plane,
  SingleSlitAperture,
  SlitAperture,
  SpiralPhasePlate,
  SpiralAdaptiveFresnelLensComponent,
  TransparentComponent,
  PhaseConjugator,
  Wedge
} from './core/components.js';

const canvas = document.querySelector('#intensity-canvas');
const context = canvas.getContext('2d');
const controls = {
  environment: document.querySelector('#environment'),
  source: document.querySelector('#source'),
  bitmapFile: document.querySelector('#bitmap-file'),
  distance: document.querySelector('#distance'),
  inspectionPoint: document.querySelector('#inspection-point')
};
let bitmapData = null;
const extraComponents = [];
const plotMode = document.querySelector('#plot-mode');
const outputs = {
  distance: document.querySelector('#distance-value')
};
const descriptions = {
  'double-slit': 'A uniform plane wave passes through two narrow openings and is focused into the far field.',
  'single-slit': 'A uniform plane wave diffracts through one opening, producing a sinc-squared envelope.',
  grating: 'A periodic aperture creates a series of sharp interference orders across the detector.',
  'mach-zehnder': 'A Mach-Zehnder interferometer splits a beam of light into two paths and recombines them.',
  hologram: 'A reference field modulates an incident field through a two-input hologram.'
};
const storageKey = 'youngtim-experiment-state';
const sourceCommonFields = [
  { key: 'wavelength', label: 'Wavelength (nm)', input: 'number', min: 200, max: 2000, step: 0.1 },
  { key: 'physicalWidth', label: 'Physical width (mm)', input: 'number', min: 0.1, max: 100, step: 0.1 },
  { key: 'physicalHeight', label: 'Physical height (mm)', input: 'number', min: 0.1, max: 100, step: 0.1 },
  { key: 'resolutionWidth', label: 'Width samples', input: 'select', options: [[64, '64'], [128, '128'], [256, '256'], [512, '512']] },
  { key: 'resolutionHeight', label: 'Height samples', input: 'select', options: [[64, '64'], [128, '128'], [256, '256'], [512, '512']] }
];
const sourceSchemas = {
  uniform: { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128, xWaveNumber: 0, yWaveNumber: 0 }, fields: [
    ...sourceCommonFields,
    { key: 'xWaveNumber', label: 'X wave number (rad/m)', input: 'number', min: -100000, max: 100000, step: 10 },
    { key: 'yWaveNumber', label: 'Y wave number (rad/m)', input: 'number', min: -100000, max: 100000, step: 10 }
  ] },
  gaussian: { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128, waist: 1, xCentre: 0, yCentre: 0, xWaveNumber: 0, yWaveNumber: 0 }, fields: [
    ...sourceCommonFields,
    { key: 'waist', label: 'Waist (mm)', input: 'number', min: 0.01, max: 50, step: 0.01 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 },
    { key: 'xWaveNumber', label: 'X wave number (rad/m)', input: 'number', min: -100000, max: 100000, step: 10 },
    { key: 'yWaveNumber', label: 'Y wave number (rad/m)', input: 'number', min: -100000, max: 100000, step: 10 }
  ] },
  'hermite-gaussian': { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128, mIndex: 1, nIndex: 1, waist: 1, xCentre: 0, yCentre: 0 }, fields: [
    ...sourceCommonFields,
    { key: 'mIndex', label: 'X mode index', input: 'number', min: 0, max: 20, step: 1 },
    { key: 'nIndex', label: 'Y mode index', input: 'number', min: 0, max: 20, step: 1 },
    { key: 'waist', label: 'Waist (mm)', input: 'number', min: 0.01, max: 50, step: 0.01 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 }
  ] },
  'laguerre-gaussian': { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128, topologicalCharge: 1, radialIndex: 0, waist: 1, xCentre: 0, yCentre: 0 }, fields: [
    ...sourceCommonFields,
    { key: 'topologicalCharge', label: 'Topological charge', input: 'number', min: -20, max: 20, step: 1 },
    { key: 'radialIndex', label: 'Radial index', input: 'number', min: 0, max: 20, step: 1 },
    { key: 'waist', label: 'Waist (mm)', input: 'number', min: 0.01, max: 50, step: 0.01 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 }
  ] },
  bessel: { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128, topologicalCharge: 0, radialWaveNumber: 1800, xCentre: 0, yCentre: 0 }, fields: [
    ...sourceCommonFields,
    { key: 'topologicalCharge', label: 'Topological charge', input: 'number', min: -20, max: 20, step: 1 },
    { key: 'radialWaveNumber', label: 'Radial wave number (1/m)', input: 'number', min: 0, max: 100000, step: 10 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -50, max: 50, step: 0.01 }
  ] },
  bitmap: { defaults: { wavelength: 633, physicalWidth: 10, physicalHeight: 10, resolutionWidth: 128, resolutionHeight: 128 }, fields: sourceCommonFields }
};
let sourceParameters = { ...sourceSchemas.uniform.defaults };
let hologramBitmapData = null;
let eitherOrMaskData = null;
let phaseConjugateMaskData = null;
const environmentPresets = {
  'double-slit': { source: 'uniform', chain: [['double-slit', {}], ['lens', { focalLength: 0.6 }]] },
  'single-slit': { source: 'uniform', chain: [['single-slit', {}]] },
  grating: { source: 'uniform', chain: [['grating', {}]] },
  'mach-zehnder': { source: 'uniform', chain: [] },
  hologram: { source: 'uniform', chain: [] }
};
const componentSchemas = {
  transparent: { defaults: {}, fields: [] },
  'clone-of': { defaults: { targetIndex: 0 }, fields: [
    { key: 'targetIndex', label: 'Component index', input: 'number', min: 0, max: 64, step: 1 }
  ] },
  'nd-filter': { defaults: { type: 'factor', value: 0.5 }, fields: [
    { key: 'type', label: 'Mode', input: 'select', options: [['factor', 'Transmission factor'], ['optical-density', 'Optical density']] },
    { key: 'value', label: 'Value', input: 'number', min: 0, max: 10, step: 0.01 }
  ] },
  'spiral-phase': { defaults: { topologicalCharge: 1 }, fields: [
    { key: 'topologicalCharge', label: 'Topological charge', input: 'number', min: -10, max: 10, step: 1 }
  ] },
  'beam-expander': { defaults: { magnification: 2 }, fields: [
    { key: 'magnification', label: 'Magnification', input: 'number', min: 0.1, max: 10, step: 0.1 }
  ] },
  'beam-transformer': { defaults: { rotationAngle: 0, zoomFactor: 1, clip: true }, fields: [
    { key: 'rotationAngle', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'zoomFactor', label: 'Zoom factor', input: 'number', min: -10, max: 10, step: 0.1 },
    { key: 'clip', label: 'Clip field', input: 'checkbox' }
  ] },
  'dove-prism': { defaults: { rotationAngle: 0 }, fields: [
    { key: 'rotationAngle', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 }
  ] },
  'dove-prism-array': { defaults: { prismWidth: 8, rotationAngle: 45 }, fields: [
    { key: 'prismWidth', label: 'Prism width (pixels)', input: 'select', options: [[1, '1'], [2, '2'], [4, '4'], [8, '8'], [16, '16'], [32, '32'], [64, '64']] },
    { key: 'rotationAngle', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 }
  ] },
  mirror: { defaults: { reflectionCoefficient: 0.99 }, fields: [
    { key: 'reflectionCoefficient', label: 'Reflection coefficient', input: 'number', min: 0, max: 1, step: 0.01 }
  ] },
  hologrammifier: { defaults: { type: 'phase', phaseStepHeightFactor: 1 }, fields: [
    { key: 'type', label: 'Mode', input: 'select', options: [['phase', 'Phase'], ['intensity', 'Intensity']] },
    { key: 'phaseStepHeightFactor', label: 'Phase step height (× 2π)', input: 'number', min: 0, max: 10, step: 0.01 }
  ] },
  'hologram-from-bitmap': { defaults: {}, fields: [] },
  'either-or': { defaults: { threshold: 0.5 }, fields: [
    { key: 'threshold', label: 'Brightness threshold', input: 'number', min: 0, max: 1, step: 0.01 }
  ] },
  'phase-conjugate-surface': { defaults: { threshold: 0.5 }, fields: [
    { key: 'threshold', label: 'Brightness threshold', input: 'number', min: 0, max: 1, step: 0.01 }
  ] },
  'fourier-lens': { defaults: { focalLength: 0.6, keepPhysicalDimensions: true }, fields: [
    { key: 'focalLength', label: 'Focal length (m)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'keepPhysicalDimensions', label: 'Keep physical dimensions', input: 'checkbox' }
  ] },
  'cylindrical-lens': { defaults: { focalLength: 0.6, axisAngle: 14.3239 }, fields: [
    { key: 'focalLength', label: 'Focal length (m)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'axisAngle', label: 'Axis angle (deg)', input: 'number', min: -180, max: 180, step: 1 }
  ] },
  'cylindrical-mode-converter': { defaults: { designWaistSize: 1, designWavelength: 632.8, axisAngle: 45 }, fields: [
    { key: 'designWaistSize', label: 'Design waist (mm)', input: 'number', min: 0.01, max: 50, step: 0.01 },
    { key: 'designWavelength', label: 'Design wavelength (nm)', input: 'number', min: 200, max: 2000, step: 0.1 },
    { key: 'axisAngle', label: 'Axis angle (deg)', input: 'number', min: -180, max: 180, step: 1 }
  ] },
  wedge: { defaults: { deflectionAngleXZ: 0.01, deflectionAngleYZ: 0 }, fields: [
    { key: 'deflectionAngleXZ', label: 'XZ deflection (rad)', input: 'number', min: -0.5, max: 0.5, step: 0.001 },
    { key: 'deflectionAngleYZ', label: 'YZ deflection (rad)', input: 'number', min: -0.5, max: 0.5, step: 0.001 }
  ] },
  'lenslet-array': { defaults: { arrayPeriod: 1, focalLength: 0.6, rotation: 0 }, fields: [
    { key: 'arrayPeriod', label: 'Array period (mm)', input: 'number', min: 0.05, max: 10, step: 0.05 },
    { key: 'focalLength', label: 'Focal length (m)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'rotation', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 }
  ] },
  'phase-conjugator': { defaults: {}, fields: [] },
  'spiral-adaptive-fresnel': { defaults: { type: 'logarithmic', p: 5.7296, b: 0.1, phi0: 0, boundary: 'half-way', azimuthalCompensation: true }, fields: [
    { key: 'type', label: 'Spiral type', input: 'select', options: [['logarithmic', 'Logarithmic'], ['archimedean', 'Archimedean'], ['fermat', 'Fermat'], ['hyperbolic', 'Hyperbolic']] },
    { key: 'p', label: 'Focusing parameter', input: 'number', min: 0.01, max: 100, step: 0.01 },
    { key: 'b', label: 'Winding parameter', input: 'number', min: 0.001, max: 10, step: 0.001 },
    { key: 'phi0', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'boundary', label: 'Winding boundary', input: 'select', options: [['half-way', 'Half-way'], ['rotated-spiral', 'Rotated spiral']] },
    { key: 'azimuthalCompensation', label: 'Azimuthal compensation', input: 'checkbox' }
  ] },
  lens: { defaults: { focalLength: 0.6 }, fields: [
    { key: 'focalLength', label: 'Focal length (m)', input: 'number', min: 0.01, max: 10, step: 0.01 }
  ] },
  distance: { defaults: { distance: 0.6 }, fields: [
    { key: 'distance', label: 'Distance (m)', input: 'number', min: 0, max: 10, step: 0.01 }
  ] },
  'double-slit': { defaults: { separation: 0.8, width: 0.12, rotation: 0, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'separation', label: 'Separation (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'width', label: 'Width (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'rotation', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  'single-slit': { defaults: { width: 0.12, rotation: 0, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'width', label: 'Width (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'rotation', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  grating: { defaults: { period: 0.8, width: 0.12 }, fields: [
    { key: 'period', label: 'Period (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'width', label: 'Width (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 }
  ] },
  gaussian: { defaults: { sigma: 0.8, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'sigma', label: 'Sigma (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  annular: { defaults: { outerRadius: 0.8, innerRadius: 0.12, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'outerRadius', label: 'Outer radius (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'innerRadius', label: 'Inner radius (mm)', input: 'number', min: 0, max: 10, step: 0.01 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  slit: { defaults: { width: 0.12, rotation: 0, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'width', label: 'Width (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'rotation', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  polygonal: { defaults: { sides: 6, radius: 0.8, rotation: 0, xCentre: 0, yCentre: 0 }, fields: [
    { key: 'sides', label: 'Sides', input: 'number', min: 3, max: 32, step: 1 },
    { key: 'radius', label: 'Radius (mm)', input: 'number', min: 0.01, max: 10, step: 0.01 },
    { key: 'rotation', label: 'Rotation (deg)', input: 'number', min: -180, max: 180, step: 1 },
    { key: 'xCentre', label: 'X centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 },
    { key: 'yCentre', label: 'Y centre (mm)', input: 'number', min: -10, max: 10, step: 0.01 }
  ] },
  absorbing: { defaults: { width: 10 }, fields: [
    { key: 'width', label: 'Boundary width (pixels)', input: 'number', min: 1, max: 64, step: 1 }
  ] },
  'aperture-stack': { defaults: { apertureType: 'grating', count: 10, separation: 1 }, fields: [
    { key: 'apertureType', label: 'Aperture type', input: 'select', options: [['grating', 'Transmission grating'], ['single-slit', 'Single slit'], ['double-slit', 'Double slit'], ['gaussian', 'Gaussian']] },
    { key: 'count', label: 'Number of apertures', input: 'number', min: 1, max: 64, step: 1 },
    { key: 'separation', label: 'Separation (mm)', input: 'number', min: 0, max: 100, step: 0.01 }
  ] }
};

function sinc(value) { return Math.abs(value) < 1e-8 ? 1 : Math.sin(value) / value; }

function updateLabels() {
  outputs.distance.value = Number(controls.distance.value).toFixed(2);
  document.querySelector('#environment-description').textContent = descriptions[controls.environment.value];
}

function renderSourceSettings() {
  const container = document.querySelector('#source-settings');
  container.replaceChildren();
  const schema = sourceSchemas[controls.source.value] ?? sourceSchemas.uniform;
  schema.fields.forEach(field => {
    const fieldLabel = document.createElement('label');
    fieldLabel.textContent = field.label;
    const input = document.createElement(field.input === 'select' ? 'select' : 'input');
    if (field.input === 'select') {
      field.options.forEach(([value, text]) => input.add(new Option(text, value)));
      input.value = sourceParameters[field.key];
    } else {
      input.type = field.input;
      input.value = sourceParameters[field.key];
      input.min = field.min;
      input.max = field.max;
      input.step = field.step;
    }
    input.addEventListener('input', () => {
      sourceParameters[field.key] = field.input === 'number' || field.key.startsWith('resolution') ? Number(input.value) : input.value;
      render();
    });
    fieldLabel.append(input);
    container.append(fieldLabel);
  });
  const isBitmap = controls.source.value === 'bitmap';
  document.querySelector('.file-label').hidden = !isBitmap;
  controls.bitmapFile.hidden = !isBitmap;
}

function getState() {
  return {
    version: 2,
    environment: controls.environment.value,
    source: controls.source.value,
    sourceParameters: { ...sourceParameters },
    distance: controls.distance.value,
    plotMode: plotMode.value,
    inspectionPoint: controls.inspectionPoint?.value ?? 'source',
    chain: extraComponents.map(item => ({ type: item.type, params: { ...item.params } }))
  };
}

function applyState(state) {
  for (const [key, value] of Object.entries(state)) {
    if (key !== 'plotMode' && key !== 'chain' && value !== undefined && controls[key]) controls[key].value = value;
  }
  const sourceType = controls.source.value;
  const sourceSchema = sourceSchemas[sourceType] ?? sourceSchemas.uniform;
  sourceParameters = { ...sourceSchema.defaults, ...(state.sourceParameters ?? {}) };
  if (state.sourceParameters === undefined && state.wavelength !== undefined) sourceParameters.wavelength = Number(state.wavelength);
  renderSourceSettings();
  if (state.plotMode) plotMode.value = state.plotMode;
  if (state.inspectionPoint && controls.inspectionPoint) {
    const requestedValue = state.inspectionPoint === 'final' ? controls.inspectionPoint.options[controls.inspectionPoint.options.length - 1]?.value ?? 'source' : state.inspectionPoint;
    controls.inspectionPoint.value = controls.inspectionPoint.options.namedItem?.(requestedValue) ? requestedValue : controls.inspectionPoint.options[controls.inspectionPoint.options.length - 1]?.value ?? 'source';
  }
  if (Array.isArray(state.chain)) {
    const restoredComponents = state.chain.map(savedItem => {
      const type = typeof savedItem === 'string' ? savedItem : savedItem.type;
      const item = makeSelectedComponent(type, typeof savedItem === 'string' ? {} : savedItem.params);
      item.label = document.querySelector(`#component-choice option[value="${type}"]`)?.textContent ?? type;
      return item;
    });
    if (state.version === undefined) {
      const item = makeSelectedComponent('double-slit');
      item.label = 'Double-slit aperture';
      restoredComponents.unshift(item);
    }
    extraComponents.splice(0, extraComponents.length, ...restoredComponents);
    renderChainList();
  }
}

function applyEnvironmentPreset(environment) {
  const preset = environmentPresets[environment] ?? environmentPresets['double-slit'];
  controls.source.value = preset.source;
  sourceParameters = { ...sourceSchemas[preset.source].defaults };
  extraComponents.splice(0, extraComponents.length, ...preset.chain.map(([type, params]) => {
    const item = makeSelectedComponent(type, params);
    item.label = document.querySelector(`#component-choice option[value="${type}"]`)?.textContent ?? type;
    return item;
  }));
  renderSourceSettings();
  renderChainList();
}

function makeLightSource(type, parameters) {
  return new LightSource({
    type,
    wavelength: parameters.wavelength * 1e-9,
    width: parameters.resolutionWidth,
    height: parameters.resolutionHeight,
    physicalWidth: parameters.physicalWidth * 1e-3,
    physicalHeight: parameters.physicalHeight * 1e-3,
    waist: (parameters.waist ?? 1) * 1e-3,
    xCentre: (parameters.xCentre ?? 0) * 1e-3,
    yCentre: (parameters.yCentre ?? 0) * 1e-3,
    xWaveNumber: parameters.xWaveNumber ?? 0,
    yWaveNumber: parameters.yWaveNumber ?? 0,
    mIndex: parameters.mIndex ?? 1,
    nIndex: parameters.nIndex ?? 1,
    topologicalCharge: parameters.topologicalCharge ?? 0,
    radialIndex: parameters.radialIndex ?? 0,
    radialWaveNumber: parameters.radialWaveNumber ?? 1800,
    bitmapData
  });
}

function imageToHologramData(image) {
  const imageCanvas = document.createElement('canvas');
  imageCanvas.width = 128;
  imageCanvas.height = 128;
  const imageContext = imageCanvas.getContext('2d');
  imageContext.drawImage(image, 0, 0, 128, 128);
  const pixels = imageContext.getImageData(0, 0, 128, 128).data;
  const hologram = new Float64Array(128 * 128 * 2);
  for (let y = 0; y < 128; y++) for (let x = 0; x < 128; x++) {
    const pixel = (y * 128 + x) * 4;
    const red = pixels[pixel] / 255;
    const green = pixels[pixel + 1] / 255;
    const blue = pixels[pixel + 2] / 255;
    const maximum = Math.max(red, green, blue);
    const minimum = Math.min(red, green, blue);
    const delta = maximum - minimum;
    let hue = 0;
    if (delta > 0) {
      if (maximum === red) hue = ((green - blue) / delta) % 6;
      else if (maximum === green) hue = (blue - red) / delta + 2;
      else hue = (red - green) / delta + 4;
      hue /= 6;
      if (hue < 0) hue += 1;
    }
    const phase = 2 * Math.PI * (hue - 0.5);
    const target = ((127 - y) * 128 + x) * 2;
    hologram[target] = maximum * Math.cos(phase);
    hologram[target + 1] = maximum * Math.sin(phase);
  }
  return hologram;
}

function makeStackAperture(type) {
  if (type === 'single-slit') return new SingleSlitAperture({ width: 0.12e-3 });
  if (type === 'double-slit') return new DoubleSlitAperture({ separation: 0.8e-3, width: 0.12e-3 });
  if (type === 'gaussian') return new GaussianAperture({ sigma: 0.8e-3 });
  return new GratingAperture({ period: 0.8e-3, width: 0.12e-3 });
}

function imageToBrightnessData(image) {
  const imageCanvas = document.createElement('canvas');
  imageCanvas.width = 128;
  imageCanvas.height = 128;
  const imageContext = imageCanvas.getContext('2d');
  imageContext.drawImage(image, 0, 0, 128, 128);
  const pixels = imageContext.getImageData(0, 0, 128, 128).data;
  const brightness = new Float64Array(128 * 128);
  for (let index = 0; index < brightness.length; index++) {
    const pixel = index * 4;
    brightness[index] = Math.max(pixels[pixel], pixels[pixel + 1], pixels[pixel + 2]) / 255;
  }
  return brightness;
}

function createSystem(parameters) {
  const source = makeLightSource(parameters.source, parameters.sourceParameters);
  if (parameters.environment === 'hologram') {
    const graph = new OpticalGraph();
    graph.addNode('hologram-field', makeLightSource('gaussian', { ...sourceSchemas.gaussian.defaults, ...parameters.sourceParameters, wavelength: parameters.sourceParameters.wavelength }));
    graph.addNode('incident-field', source);
    graph.addNode('hologram', new Hologram(), { inputs: 2 });
    graph.addNode('output', new Plane('Hologram output'));
    graph.connect('hologram-field', 0, 'hologram', 0)
      .connect('incident-field', 0, 'hologram', 1)
      .connect('hologram', 0, 'output', 0);
    return { run: () => graph.run().planes[0].snapshot() };
  }
  if (parameters.environment === 'mach-zehnder') {
    const graph = new OpticalGraph();
    graph.addNode('source', source);
    graph.addNode('splitter-a', new BeamSplitter(), { inputs: 2, outputs: 2 });
    graph.addNode('upper-arm', new Distance(parameters.distance / 2));
    graph.addNode('lower-arm', new Lens(0.6));
    graph.addNode('splitter-b', new BeamSplitter(), { inputs: 2, outputs: 2 });
    graph.addNode('output', new Plane('Interferometer output'));
    graph.connect('source', 0, 'splitter-a', 0)
      .connect('splitter-a', 0, 'upper-arm', 0)
      .connect('splitter-a', 1, 'lower-arm', 0)
      .connect('upper-arm', 0, 'splitter-b', 0)
      .connect('lower-arm', 0, 'splitter-b', 1)
      .connect('splitter-b', 0, 'output', 0);
    return { run: () => graph.run().planes[0].snapshot() };
  }
  const components = [source];
  const chain = extraComponents.map(item => item.component);
  extraComponents.forEach((item, index) => {
    if (item.type === 'clone-of') {
      const targetIndex = Math.round(item.params.targetIndex);
      item.component.delegate = targetIndex >= 0 && targetIndex < chain.length && targetIndex !== index ? chain[targetIndex] : null;
    }
  });
  components.push(...chain);
  components.push(new Distance(parameters.distance), new Plane('Far field'));
  return new OpticalSystem(components);
}

function makeSelectedComponent(type, savedParams = {}) {
  const schema = componentSchemas[type] ?? componentSchemas.transparent;
  const params = { ...schema.defaults, ...savedParams };
  let component = new TransparentComponent();
  if (type === 'nd-filter') component = new NeutralDensityFilter(params);
  else if (type === 'spiral-phase') component = new SpiralPhasePlate(params.topologicalCharge);
  else if (type === 'beam-expander') component = new BeamExpander(params.magnification);
  else if (type === 'beam-transformer') component = new BeamTransformer(params.rotationAngle * Math.PI / 180, params.zoomFactor, params.clip);
  else if (type === 'dove-prism') component = new DovePrism(params.rotationAngle * Math.PI / 180);
  else if (type === 'dove-prism-array') component = new DovePrismArray(params.prismWidth, params.rotationAngle * Math.PI / 180);
  else if (type === 'mirror') component = new Mirror(params.reflectionCoefficient);
  else if (type === 'hologrammifier') component = new Hologrammifier(params.type, params.phaseStepHeightFactor);
  else if (type === 'hologram-from-bitmap') component = new HologramFromBitmap(hologramBitmapData);
  else if (type === 'either-or') component = new EitherOrComponent(eitherOrMaskData, params.threshold);
  else if (type === 'phase-conjugate-surface') component = new EitherOrPhaseConjugateSurface(phaseConjugateMaskData, params.threshold);
  else if (type === 'aperture-stack') component = new ApertureStack(makeStackAperture(params.apertureType), params.count, params.separation * 1e-3);
  else if (type === 'fourier-lens') component = new FourierLens(params.focalLength, params.keepPhysicalDimensions);
  else if (type === 'cylindrical-lens') component = new CylindricalLens(params.focalLength, params.axisAngle * Math.PI / 180);
  else if (type === 'cylindrical-mode-converter') component = new CylindricalLensModeConverter(params.designWaistSize * 1e-3, params.designWavelength * 1e-9, params.axisAngle * Math.PI / 180);
  else if (type === 'clone-of') component = new CloneOfComponent();
  else if (type === 'wedge') component = new Wedge(params.deflectionAngleXZ, params.deflectionAngleYZ);
  else if (type === 'lenslet-array') component = new LensletArray(params.arrayPeriod * 1e-3, params.focalLength, params.rotation * Math.PI / 180);
  else if (type === 'phase-conjugator') component = new PhaseConjugator();
  else if (type === 'spiral-adaptive-fresnel') component = new SpiralAdaptiveFresnelLensComponent({ ...params, phi0: params.phi0 * Math.PI / 180 });
  else if (type === 'lens') component = new Lens(params.focalLength);
  else if (type === 'distance') component = new Distance(params.distance);
  else if (type === 'double-slit') component = new DoubleSlitAperture({ separation: params.separation * 1e-3, width: params.width * 1e-3, rotation: params.rotation * Math.PI / 180, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'single-slit') component = new SingleSlitAperture({ width: params.width * 1e-3, rotation: params.rotation * Math.PI / 180, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'grating') component = new GratingAperture({ period: params.period * 1e-3, width: params.width * 1e-3 });
  else if (type === 'gaussian') component = new GaussianAperture({ sigma: params.sigma * 1e-3, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'annular') component = new AnnularAperture({ outerRadius: params.outerRadius * 1e-3, innerRadius: params.innerRadius * 1e-3, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'slit') component = new SlitAperture({ width: params.width * 1e-3, rotation: params.rotation * Math.PI / 180, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'polygonal') component = new PolygonalAperture({ sides: params.sides, radius: params.radius * 1e-3, rotation: params.rotation * Math.PI / 180, xCentre: params.xCentre * 1e-3, yCentre: params.yCentre * 1e-3 });
  else if (type === 'absorbing') component = new AbsorbingBoundary(params.width);
  return { component, type, params };
}

function renderChainList() {
  const list = document.querySelector('#chain-list');
  list.replaceChildren();
  extraComponents.forEach((item, index) => {
    const row = document.createElement('li');
    const header = document.createElement('div');
    header.className = 'component-row-header';
    const label = document.createElement('span');
    label.textContent = item.label;
    const actions = document.createElement('span');
    const moveUp = document.createElement('button');
    moveUp.type = 'button';
    moveUp.title = `Move ${item.label} earlier`;
    moveUp.setAttribute('aria-label', `Move ${item.label} earlier`);
    moveUp.textContent = '↑';
    moveUp.disabled = index === 0;
    moveUp.addEventListener('click', () => {
      [extraComponents[index - 1], extraComponents[index]] = [extraComponents[index], extraComponents[index - 1]];
      renderChainList();
      render();
    });
    const moveDown = document.createElement('button');
    moveDown.type = 'button';
    moveDown.title = `Move ${item.label} later`;
    moveDown.setAttribute('aria-label', `Move ${item.label} later`);
    moveDown.textContent = '↓';
    moveDown.disabled = index === extraComponents.length - 1;
    moveDown.addEventListener('click', () => {
      [extraComponents[index], extraComponents[index + 1]] = [extraComponents[index + 1], extraComponents[index]];
      renderChainList();
      render();
    });
    const remove = document.createElement('button');
    remove.type = 'button';
    remove.title = `Remove ${item.label}`;
    remove.setAttribute('aria-label', `Remove ${item.label}`);
    remove.textContent = '×';
    remove.addEventListener('click', () => {
      extraComponents.splice(index, 1);
      renderChainList();
      render();
    });
    actions.append(moveUp, moveDown, remove);
    header.append(label, actions);
    row.append(header);
    const schema = componentSchemas[item.type] ?? componentSchemas.transparent;
    if (schema.fields.length) {
      const settings = document.createElement('div');
      settings.className = 'component-settings';
      schema.fields.filter(field => !(item.type === 'hologrammifier' && item.params.type === 'intensity' && field.key === 'phaseStepHeightFactor')).forEach(field => {
        const fieldLabel = document.createElement('label');
        fieldLabel.textContent = field.label;
        const input = document.createElement(field.input === 'select' ? 'select' : 'input');
        if (field.input !== 'select') input.type = field.input;
        if (field.input === 'select') {
          field.options.forEach(([value, text]) => input.add(new Option(text, value)));
          input.value = item.params[field.key];
        } else if (field.input === 'checkbox') {
          input.checked = Boolean(item.params[field.key]);
        } else {
          input.value = item.params[field.key];
          input.min = field.min;
          input.max = field.max;
          input.step = field.step;
        }
        input.addEventListener('input', () => {
          item.params[field.key] = field.input === 'checkbox' ? input.checked : field.input === 'number' ? Number(input.value) : input.value;
          item.component = makeSelectedComponent(item.type, item.params).component;
          if (item.type === 'hologrammifier' && field.key === 'type') renderChainList();
          render();
        });
        fieldLabel.append(input);
        settings.append(fieldLabel);
      });
      row.append(settings);
    }
    if (item.type === 'hologram-from-bitmap') {
      const bitmapLabel = document.createElement('label');
      bitmapLabel.textContent = 'Hologram bitmap';
      const bitmapInput = document.createElement('input');
      bitmapInput.type = 'file';
      bitmapInput.accept = 'image/*';
      bitmapInput.addEventListener('change', () => {
        const file = bitmapInput.files?.[0];
        if (!file) return;
        const image = new Image();
        image.onload = () => {
          hologramBitmapData = imageToHologramData(image);
          item.component = new HologramFromBitmap(hologramBitmapData);
          render();
          URL.revokeObjectURL(image.src);
        };
        image.src = URL.createObjectURL(file);
      });
      bitmapLabel.append(bitmapInput);
      row.append(bitmapLabel);
    }
    if (item.type === 'either-or') {
      const bitmapLabel = document.createElement('label');
      bitmapLabel.textContent = 'Selection mask';
      const bitmapInput = document.createElement('input');
      bitmapInput.type = 'file';
      bitmapInput.accept = 'image/*';
      bitmapInput.addEventListener('change', () => {
        const file = bitmapInput.files?.[0];
        if (!file) return;
        const image = new Image();
        image.onload = () => {
          eitherOrMaskData = imageToBrightnessData(image);
          item.component = new EitherOrComponent(eitherOrMaskData, item.params.threshold);
          render();
          URL.revokeObjectURL(image.src);
        };
        image.src = URL.createObjectURL(file);
      });
      bitmapLabel.append(bitmapInput);
      row.append(bitmapLabel);
    }
    if (item.type === 'phase-conjugate-surface') {
      const bitmapLabel = document.createElement('label');
      bitmapLabel.textContent = 'Conjugation mask';
      const bitmapInput = document.createElement('input');
      bitmapInput.type = 'file';
      bitmapInput.accept = 'image/*';
      bitmapInput.addEventListener('change', () => {
        const file = bitmapInput.files?.[0];
        if (!file) return;
        const image = new Image();
        image.onload = () => {
          phaseConjugateMaskData = imageToBrightnessData(image);
          item.component = new EitherOrPhaseConjugateSurface(phaseConjugateMaskData, item.params.threshold);
          render();
          URL.revokeObjectURL(image.src);
        };
        image.src = URL.createObjectURL(file);
      });
      bitmapLabel.append(bitmapInput);
      row.append(bitmapLabel);
    }
    list.append(row);
  });
}

function fieldValue(beam, index, mode) {
  if (mode === 'amplitude') return Math.hypot(beam.real[index], beam.imaginary[index]);
  if (mode === 'real') return beam.real[index];
  if (mode === 'imaginary') return beam.imaginary[index];
  if (mode === 'phase') return (Math.atan2(beam.imaginary[index], beam.real[index]) + Math.PI) / (2 * Math.PI);
  return beam.real[index] ** 2 + beam.imaginary[index] ** 2;
}

function heatColour(value, mode) {
  const stops = mode === 'intensity'
    ? [[0, 0, 0], [20, 137, 148], [238, 177, 76]]
    : [[12, 25, 48], [20, 137, 148], [238, 177, 76]];
  const scaled = Math.max(0, Math.min(1, value)) * (stops.length - 1);
  const lower = Math.floor(scaled);
  const upper = Math.min(stops.length - 1, lower + 1);
  const fraction = scaled - lower;
  return stops[lower].map((channel, index) => Math.round(channel + (stops[upper][index] - channel) * fraction));
}

function phaseIntensityColour(phase, intensity) {
  // Clamp intensity to [0,1]
  const v = Math.max(0, Math.min(1, intensity));

  // Wrap phase into [0,1); note that the "+ 1 % 1" makes it work for negative values also
  const h = ((phase % 1) + 1) % 1;

  const s = 1;

  const i = Math.floor(h * 6);
  const f = h * 6 - i;

  const p = v * (1 - s);
  const q = v * (1 - f * s);
  const t = v * (1 - (1 - f) * s);

  let r, g, b;

  switch (i % 6) {
    case 0: r = v; g = t; b = p; break;
    case 1: r = q; g = v; b = p; break;
    case 2: r = p; g = v; b = t; break;
    case 3: r = p; g = q; b = v; break;
    case 4: r = t; g = p; b = v; break;
    case 5: r = v; g = p; b = q; break;
  }

  return [
    Math.round(r * 255),
    Math.round(g * 255),
    Math.round(b * 255)
  ];
}

function updatePlotAxes(beam, isTwoDimensional, isFourierPlot = false) {
  const xMin = document.querySelector('#axis-x-min');
  const xMax = document.querySelector('#axis-x-max');
  const xLabel = document.querySelector('#axis-x-label');
  const yTop = document.querySelector('#axis-y-top');
  const yBottom = document.querySelector('#axis-y-bottom');
  const yLabel = document.querySelector('#axis-y-label');

  if (isTwoDimensional) {
    const formatLabel = (value, unit) => {
      const normalized = Object.is(value, -0) ? 0 : value;
      const sign = normalized === 0 ? '' : normalized > 0 ? '+' : '-';
      return `${sign}${Math.abs(normalized).toFixed(1)} ${unit}`;
    };

    if (isFourierPlot) {
      const xRange = beam.width / (2 * Math.max(beam.physicalWidth, 1e-12)) * 1e-3;
      const yRange = beam.height / (2 * Math.max(beam.physicalHeight, 1e-12)) * 1e-3;
      xMin.textContent = formatLabel(-xRange, '1/mm');
      xMax.textContent = formatLabel(xRange, '1/mm');
      xLabel.textContent = 'x frequency';
      yTop.textContent = formatLabel(yRange, '1/mm');
      yBottom.textContent = formatLabel(-yRange, '1/mm');
      yLabel.textContent = 'y frequency';
      return;
    }

    const xValues = [beam.xCoordinate(0), beam.xCoordinate(beam.width - 1)].map(value => value * 1000);
    const yValues = [beam.yCoordinate(0), beam.yCoordinate(beam.height - 1)].map(value => value * 1000);
    xMin.textContent = formatLabel(Math.min(...xValues), 'mm');
    xMax.textContent = formatLabel(Math.max(...xValues), 'mm');
    xLabel.textContent = 'x position';
    yTop.textContent = formatLabel(Math.max(...yValues), 'mm');
    yBottom.textContent = formatLabel(Math.min(...yValues), 'mm');
    yLabel.textContent = 'y position';
    return;
  }

  xMin.textContent = '-6 mm';
  xMax.textContent = '+6 mm';
  xLabel.textContent = 'screen position';
  yTop.textContent = '1.0';
  yBottom.textContent = '0';
  yLabel.textContent = 'normalised';
}

function positionPlotAxes(bounds) {
  const xAxis = document.querySelector('.axis-x');
  const yAxis = document.querySelector('.axis-y');
  const plotWrap = document.querySelector('.plot-wrap');
  if (!xAxis || !yAxis || !plotWrap) return;

  xAxis.style.left = '';
  xAxis.style.right = '';
  xAxis.style.top = '';
  xAxis.style.bottom = '';
  xAxis.style.width = '';

  yAxis.style.left = '';
  yAxis.style.top = '';
  yAxis.style.bottom = '';
  yAxis.style.right = '';
  yAxis.style.height = '';
  yAxis.style.width = '';
}

function renderTwoDimensional(beam, mode) {
  const width = canvas.width;
  const height = canvas.height;
  context.setTransform(1, 0, 0, 1, 0, 0);
  context.clearRect(0, 0, width, height);
  const aspectRatio = beam.physicalWidth / beam.physicalHeight;
  const canvasRatio = width / height;
  const imageWidth = Math.max(1, Math.round(aspectRatio > canvasRatio ? width : height * aspectRatio));
  const imageHeight = Math.max(1, Math.round(imageWidth / aspectRatio));
  const offsetX = Math.round((width - imageWidth) / 2);
  const offsetY = Math.round((height - imageHeight) / 2);
  positionPlotAxes({ offsetX, offsetY, imageWidth, imageHeight });
  const values = new Float64Array(beam.width * beam.height);
  let maximum = 0;
  for (let index = 0; index < values.length; index++) {
    values[index] = fieldValue(beam, index, mode);
    maximum = Math.max(maximum, values[index]);
  }
  context.fillStyle = '#0c1930';
  context.fillRect(0, 0, width, height);
  const image = context.createImageData(imageWidth, imageHeight);
  for (let y = 0; y < imageHeight; y++) {
    const sourceY = Math.min(beam.height - 1, Math.floor(y / imageHeight * beam.height));
    for (let x = 0; x < imageWidth; x++) {
      const sourceX = Math.min(beam.width - 1, Math.floor(x / imageWidth * beam.width));
      const sourceIndex = sourceY * beam.width + sourceX;
      const normalized = mode === 'phase' ? values[sourceIndex] : values[sourceIndex] / Math.max(maximum, 1e-12);
      // const [red, green, blue] = heatColour(normalized, mode);
      const [red, green, blue] = mode === 'phase-intensity' ? phaseIntensityColour(
        Math.atan2(beam.imaginary[sourceIndex], beam.real[sourceIndex]) / (2 * Math.PI), 
        normalized
      ) : heatColour(normalized, mode);
      const pixelIndex = (y * imageWidth + x) * 4;
      image.data[pixelIndex] = red;
      image.data[pixelIndex + 1] = green;
      image.data[pixelIndex + 2] = blue;
      image.data[pixelIndex + 3] = 255;
    }
  }
  context.putImageData(image, offsetX, offsetY);
  return maximum;
}

function getFourierIntensityBeam(beam) {
  const transformed = beam.clone();
  transformed.swapQuadrants();
  transformed.fourierTransform(true);
  transformed.swapQuadrants();
  return transformed;
}

function buildInspectionPointOptions() {
  if (!controls.inspectionPoint) return;
  const current = controls.inspectionPoint.value;
  const options = [{ value: 'source', label: 'After light source' }];
  extraComponents.forEach((item, index) => {
    options.push({ value: `after:${index}`, label: `After ${item.label}` });
  });
  controls.inspectionPoint.replaceChildren();
  options.forEach(({ value, label }) => controls.inspectionPoint.add(new Option(label, value)));
  controls.inspectionPoint.value = options.some(option => option.value === current) ? current : options.at(-1)?.value ?? 'source';
}

function getBeamInspectionData(parameters) {
  const source = makeLightSource(parameters.source, parameters.sourceParameters);
  let beam = source.output({
    width: source.width,
    height: source.height,
    physicalWidth: source.physicalWidth,
    physicalHeight: source.physicalHeight
  });
  const entries = [{ key: 'source', label: 'After light source', beam: beam.clone() }];
  extraComponents.forEach((item, index) => {
    const result = item.component.apply(beam);
    beam = Array.isArray(result) ? result[0] : result;
    entries.push({ key: `after:${index}`, label: `After ${item.label}`, beam: beam.clone() });
  });
  return { beam, entries };
}

function render() {
  updateLabels();
  const bounds = canvas.getBoundingClientRect();
  const pixelRatio = window.devicePixelRatio || 1;
  canvas.width = Math.max(1, Math.round(bounds.width * pixelRatio));
  canvas.height = Math.max(1, Math.round(bounds.height * pixelRatio));
  context.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);
  const width = bounds.width;
  const height = bounds.height;
  const parameters = {
    environment: controls.environment.value,
    source: controls.source.value,
    sourceParameters,
    distance: Number(controls.distance.value)
  };
  buildInspectionPointOptions();
  const system = createSystem(parameters);
  const finalBeam = system.run();
  const inspectionData = getBeamInspectionData(parameters);
  const inspectionKey = controls.inspectionPoint?.value ?? inspectionData.entries.at(-1)?.key ?? 'source';
  const selectedInspection = inspectionData.entries.find(entry => entry.key === inspectionKey) ?? inspectionData.entries.at(-1) ?? { key: 'source', label: 'After light source', beam: finalBeam };
  const beam = selectedInspection?.beam ?? finalBeam;
  const plottedBeam = new Distance(parameters.distance).apply(beam.clone());
  const isFourierIntensity = plotMode.value === 'fourier-intensity' || plotMode.value === '2d-fourier-intensity';
  const displayedBeam = isFourierIntensity ? getFourierIntensityBeam(plottedBeam) : plottedBeam;
  const isTwoDimensional = plotMode.value.startsWith('2d-');
  const isFourierPlot = plotMode.value === '2d-fourier-intensity';
  const selectedMode = plotMode.value.replace('2d-', '');
  const visibleMode = selectedMode === 'fourier-intensity' ? 'intensity' : selectedMode;
  updatePlotAxes(displayedBeam, isTwoDimensional, isFourierPlot);
  if (isTwoDimensional) {
    const maximum = renderTwoDimensional(displayedBeam, visibleMode);
    const modeLabel = plotMode.selectedOptions[0].textContent;
    document.querySelector('#plot-title').textContent = selectedInspection?.label ? `${selectedInspection.label} / ${modeLabel}` : `${modeLabel} field`;
    document.querySelector('#plot-eyebrow').textContent = `02 / ${modeLabel}`;
    document.querySelector('#peak-readout').textContent = `Peak ${maximum.toExponential(2)}`;
    document.querySelector('#resolution-readout').textContent = `${displayedBeam.width} × ${displayedBeam.height} field`;
    document.querySelector('#plot-legend').innerHTML = `<i class="legend-swatch"></i> ${modeLabel}`;
    document.querySelector('#run-state').textContent = `Calculated at ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
    localStorage.setItem(storageKey, JSON.stringify(getState()));
    return;
  }
  context.setTransform(pixelRatio, 0, 0, pixelRatio, 0, 0);
  let sourceValues;
  if (plotMode.value === 'real') sourceValues = displayedBeam.realPartRow();
  else if (plotMode.value === 'imaginary') sourceValues = displayedBeam.imaginaryPartRow();
  else if (plotMode.value === 'phase') sourceValues = displayedBeam.phaseRow().map(value => (value + Math.PI) / (2 * Math.PI));
  else if (plotMode.value === 'fourier-intensity') sourceValues = displayedBeam.intensityRow();
  else sourceValues = displayedBeam.intensityRow();
  const samples = sourceValues.length;
  const values = Array.from(sourceValues);
  const maximum = Math.max(...values, 1e-8);
  const titles = {
    'double-slit': 'Double-slit interference',
    'single-slit': 'Single-slit diffraction',
    grating: 'Transmission grating orders'
  };
  const aperture = extraComponents.find(item => ['double-slit', 'single-slit', 'grating'].includes(item.type));
  const inspectionTitle = selectedInspection?.label ?? 'Beam profile';
  document.querySelector('#plot-title').textContent = plotMode.value === 'fourier-intensity'
    ? `${inspectionTitle} / Fourier transform (intensity)`
    : parameters.environment === 'mach-zehnder' && inspectionKey === 'source'
      ? 'Mach-Zehnder output'
      : aperture && inspectionKey === 'source'
        ? titles[aperture.type] ?? `${aperture.label} intensity`
        : inspectionTitle;
  document.querySelector('#peak-readout').textContent = `Peak ${maximum.toExponential(2)}`;
  document.querySelector('#plot-eyebrow').textContent = `02 / ${plotMode.selectedOptions[0].textContent}`;
  document.querySelector('#plot-legend').innerHTML = `<i class="legend-swatch"></i> ${plotMode.value === 'fourier-intensity' ? 'Fourier intensity spectrum' : dependenceLabelText(inspectionTitle)}`;
  context.clearRect(0, 0, width, height);
  context.beginPath();
  values.forEach((value, index) => {
    const x = index / (samples - 1) * width;
    const y = height - (value / maximum) * (height - 24) - 12;
    if (index === 0) context.moveTo(x, y); else context.lineTo(x, y);
  });
  context.lineTo(width, height);
  context.lineTo(0, height);
  context.closePath();
  context.fillStyle = 'rgba(231, 111, 60, .16)';
  context.fill();
  context.beginPath();
  values.forEach((value, index) => {
    const x = index / (samples - 1) * width;
    const y = height - (value / maximum) * (height - 24) - 12;
    if (index === 0) context.moveTo(x, y); else context.lineTo(x, y);
  });
  context.strokeStyle = '#e76f3c';
  context.lineWidth = 2;
  context.stroke();
  document.querySelector('#resolution-readout').textContent = `${samples} samples`;
  document.querySelector('#run-state').textContent = `Calculated at ${new Date().toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}`;
  localStorage.setItem(storageKey, JSON.stringify(getState()));
}

function dependenceLabelText(label) {
  if (!label) return 'Normalised intensity';
  return `${label} intensity`;
}

controls.environment.addEventListener('change', () => {
  applyEnvironmentPreset(controls.environment.value);
  render();
});
controls.source.addEventListener('change', () => {
  const previous = sourceParameters;
  sourceParameters = { ...sourceSchemas[controls.source.value].defaults };
  for (const key of ['wavelength', 'physicalWidth', 'physicalHeight', 'resolutionWidth', 'resolutionHeight']) {
    if (previous[key] !== undefined && sourceParameters[key] !== undefined) sourceParameters[key] = previous[key];
  }
  renderSourceSettings();
  render();
});
Object.entries(controls).forEach(([name, control]) => {
  if (name !== 'bitmapFile' && name !== 'source') control.addEventListener('input', render);
});
controls.bitmapFile.addEventListener('change', () => {
  const file = controls.bitmapFile.files?.[0];
  if (!file) return;
  const image = new Image();
  image.onload = () => {
    const bitmapCanvas = document.createElement('canvas');
    bitmapCanvas.width = 128;
    bitmapCanvas.height = 128;
    const bitmapContext = bitmapCanvas.getContext('2d');
    bitmapContext.drawImage(image, 0, 0, 128, 128);
    const imageData = bitmapContext.getImageData(0, 0, 128, 128).data;
    bitmapData = new Float64Array(128 * 128);
    for (let index = 0; index < bitmapData.length; index++) {
      const pixel = index * 4;
      bitmapData[index] = (0.2126 * imageData[pixel] + 0.7152 * imageData[pixel + 1] + 0.0722 * imageData[pixel + 2]) / 255;
    }
    controls.source.value = 'bitmap';
    sourceParameters = { ...sourceSchemas.bitmap.defaults, ...sourceParameters };
    renderSourceSettings();
    render();
    URL.revokeObjectURL(image.src);
  };
  image.src = URL.createObjectURL(file);
});
document.querySelector('#add-component').addEventListener('click', () => {
  const choice = document.querySelector('#component-choice');
  const item = makeSelectedComponent(choice.value);
  item.label = choice.selectedOptions[0].textContent;
  extraComponents.push(item);
  renderChainList();
  render();
});
plotMode.addEventListener('input', render);
controls.inspectionPoint.addEventListener('input', render);
document.querySelector('#save-state').addEventListener('click', () => {
  const state = getState();
  const url = URL.createObjectURL(new Blob([JSON.stringify(state, null, 2)], { type: 'application/json' }));
  const download = document.createElement('a');
  download.href = url;
  download.download = 'youngtim-experiment.json';
  download.click();
  URL.revokeObjectURL(url);
});
document.querySelector('#export-plot').addEventListener('click', () => {
  const download = document.createElement('a');
  download.href = canvas.toDataURL('image/png');
  download.download = 'youngtim-plot.png';
  download.click();
});
document.querySelector('#load-state').addEventListener('change', async event => {
  const file = event.target.files?.[0];
  if (!file) return;
  const state = JSON.parse(await file.text());
  applyState(state);
  render();
});
document.querySelector('#run-button').addEventListener('click', render);
window.addEventListener('resize', render);
let restoredState = false;
try {
  const savedState = JSON.parse(localStorage.getItem(storageKey) ?? 'null');
  if (savedState) {
    restoredState = true;
    applyState(savedState);
  }
} catch {
  localStorage.removeItem(storageKey);
}
renderSourceSettings();
if (!restoredState) applyEnvironmentPreset(controls.environment.value);
render();