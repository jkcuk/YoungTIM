import test from 'node:test';
import assert from 'node:assert/strict';
import { Beam } from './beam.js';

test('FFT intensity display centers the spectrum', () => {
  const beam = new Beam(4, 4, 1, 1, 632.8e-9);
  beam.real.fill(0);
  beam.imaginary.fill(0);
  beam.real[0] = 1;

  const transformed = beam.clone();
  transformed.swapQuadrants();
  transformed.fourierTransform(true);
  transformed.swapQuadrants();

  const maximum = transformed.maxIntensity();
  const centreIndex = transformed.index(2, 2);

  assert.ok(maximum > 0);
  assert.ok(transformed.intensityAt(2, 2) >= transformed.intensityAt(0, 0));
  assert.ok(transformed.intensityAt(2, 2) >= transformed.intensityAt(1, 0));
  assert.ok(centreIndex >= 0);
});
