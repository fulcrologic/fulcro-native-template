import { NativeModulesProxy, EventEmitter, Subscription } from 'expo-modules-core';

// Import the native module. On web, it will be resolved to CatGif.web.ts
// and on native platforms to CatGif.ts
import CatGifModule from './src/CatGifModule';
import CatGifView from './src/CatGifView';
import { ChangeEventPayload, CatGifViewProps } from './src/CatGif.types';

// Get the native constant value.
export const PI = CatGifModule.PI;

export function getCatGif(url: string, fontSize: number, fontColour: number): string {
  return CatGifModule.getCatGif(url, fontSize, fontColour);
}

export async function setValueAsync(value: string) {
  return await CatGifModule.setValueAsync(value);
}

const emitter = new EventEmitter(CatGifModule ?? NativeModulesProxy.CatGif);

export function addChangeListener(listener: (event: ChangeEventPayload) => void): Subscription {
  return emitter.addListener<ChangeEventPayload>('onChange', listener);
}

export { CatGifView, CatGifViewProps, ChangeEventPayload };
