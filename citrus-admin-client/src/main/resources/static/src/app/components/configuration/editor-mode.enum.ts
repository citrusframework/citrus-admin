export enum EditorMode {
    NEW, EDIT
}

export type EditorDataTupel<T> = [EditorMode, T];