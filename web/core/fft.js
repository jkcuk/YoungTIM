export function fft1d(real, imaginary, inverse = false) {
  const size = real.length;
  if ((size & (size - 1)) !== 0) throw new RangeError('FFT size must be a power of two');
  for (let target = 1, source = 0; target < size; target++) {
    let bit = size >> 1;
    for (; source & bit; bit >>= 1) source ^= bit;
    source ^= bit;
    if (target < source) {
      [real[target], real[source]] = [real[source], real[target]];
      [imaginary[target], imaginary[source]] = [imaginary[source], imaginary[target]];
    }
  }
  for (let length = 2; length <= size; length <<= 1) {
    const angle = (inverse ? 2 : -2) * Math.PI / length;
    const stepReal = Math.cos(angle);
    const stepImaginary = Math.sin(angle);
    for (let start = 0; start < size; start += length) {
      let factorReal = 1;
      let factorImaginary = 0;
      const half = length >> 1;
      for (let offset = 0; offset < half; offset++) {
        const even = start + offset;
        const odd = even + half;
        const productReal = factorReal * real[odd] - factorImaginary * imaginary[odd];
        const productImaginary = factorReal * imaginary[odd] + factorImaginary * real[odd];
        real[odd] = real[even] - productReal;
        imaginary[odd] = imaginary[even] - productImaginary;
        real[even] += productReal;
        imaginary[even] += productImaginary;
        [factorReal, factorImaginary] = [
          factorReal * stepReal - factorImaginary * stepImaginary,
          factorReal * stepImaginary + factorImaginary * stepReal
        ];
      }
    }
  }
  if (inverse) {
    for (let index = 0; index < size; index++) {
      real[index] /= size;
      imaginary[index] /= size;
    }
  }
}

export function fft2d(real, imaginary, width, height, inverse = false) {
  const rowReal = new Float64Array(width);
  const rowImaginary = new Float64Array(width);
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      const index = y * width + x;
      rowReal[x] = real[index];
      rowImaginary[x] = imaginary[index];
    }
    fft1d(rowReal, rowImaginary, inverse);
    for (let x = 0; x < width; x++) {
      const index = y * width + x;
      real[index] = rowReal[x];
      imaginary[index] = rowImaginary[x];
    }
  }
  const columnReal = new Float64Array(height);
  const columnImaginary = new Float64Array(height);
  for (let x = 0; x < width; x++) {
    for (let y = 0; y < height; y++) {
      const index = y * width + x;
      columnReal[y] = real[index];
      columnImaginary[y] = imaginary[index];
    }
    fft1d(columnReal, columnImaginary, inverse);
    for (let y = 0; y < height; y++) {
      const index = y * width + x;
      real[index] = columnReal[y];
      imaginary[index] = columnImaginary[y];
    }
  }
}