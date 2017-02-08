export interface Directory {
    name:string;
    path:string;
    isOpen:boolean;
    children:Directory[];
}