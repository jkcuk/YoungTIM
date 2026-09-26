# YoungTIM browser port

This directory is the staged browser rewrite of the original Java/Swing application. It runs without a build step by opening `index.html` in a modern browser.

## Current stage

The first numerical stage ports the shared optical field model and a working component chain:

- complex-valued 2D beam fields stored in typed arrays
- radix-2 1D and 2D Fourier transforms
- uniform and Gaussian light sources
- Hermite-Gaussian, Laguerre-Gaussian, and Bessel light sources
- single-slit, double-slit, grating, Gaussian, annular, slit, polygonal, and absorbing-boundary apertures
- bitmap beam loading through the browser file picker
- source-specific controls for wavelength, physical area, resolution, beam modes, centres, and bitmap scaling
- numbered-port optical graphs with cached branching evaluation
- two-input/two-output beam splitters and a Mach-Zehnder interferometer example
- two-input hologram modulation and a hologram graph experiment
- mirrors, neutral-density filters, spiral phase plates, beam expanders, and Fourier lenses
- beam transformers and Dove prisms with editable rotation, zoom, and clipping behavior
- Dove-prism arrays with editable prism width and rotation
- mirrors in linear chains use the reflected output and expose reflection coefficient
- hologrammifier phase and intensity conversions with editable phase-step height
- Hologram from bitmap with brightness-to-amplitude and hue-to-phase modulation
- either-or surfaces with bitmap brightness masks for phase conjugation
- phase-conjugate surfaces with bitmap-selected per-pixel conjugation
- spiral adaptive Fresnel lens phase components with selectable winding geometry
- clone-of components that reuse another chain component by index
- aperture stacks with repeated aperture propagation and editable count/separation
- cylindrical-lens mode converter with editable design waist, wavelength, and axis
- cylindrical lenses, wedges, lenslet arrays, and phase conjugators
- an interactive linear component-chain editor with reorder and removal controls
- editable parameters for every linear component in the chain
- apertures are explicit, editable chain components and can be removed or reordered
- intensity, amplitude, and phase cross-section views
- JSON experiment save/load for browser sessions
- local browser persistence for the active experiment and PNG plot export
- thin lenses and angular-spectrum free-space propagation
- snapshot planes and sequential optical systems
- responsive canvas plotting and browser controls

The source of truth for the port is still the Java implementation under `src/`. New browser components should preserve the Java component contract: receive a beam, mutate or snapshot it, and return the beam for the next component.

## Next stages

1. Add the remaining optical components and graph editing interactions.
2. Port plot types, environment persistence, and image export.
3. Replace the prototype controls with the full workbench editor and environment factory.