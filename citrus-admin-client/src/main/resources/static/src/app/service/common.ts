import {Headers, RequestOptions} from "@angular/http";

export const JsonHeader = new RequestOptions({ headers: new Headers({ 'Content-Type': 'application/json' }) })