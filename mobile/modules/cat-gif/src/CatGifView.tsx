import { requireNativeViewManager } from 'expo-modules-core';
import * as React from 'react';

import { CatGifViewProps } from './CatGif.types';

const NativeView: React.ComponentType<CatGifViewProps> =
  requireNativeViewManager('CatGif');

export default function CatGifView(props: CatGifViewProps) {
  return <NativeView {...props} />;
}
