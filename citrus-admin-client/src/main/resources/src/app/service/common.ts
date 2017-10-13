import {Headers, RequestOptions, Response} from "@angular/http";

export const JsonHeader = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) })

export const TextHeader = new RequestOptions({ headers: new Headers({ 'Content-Type': 'text/plain' }) })

export const toJson = () => (res:Response) => res.json();