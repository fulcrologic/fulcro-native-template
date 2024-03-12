import * as React from 'react';

import { CatGifViewProps } from './CatGif.types';

export default function CatGifView(props: CatGifViewProps) {
  return (
    <div>
      <span>{props.name}</span>
    </div>
  );
}
