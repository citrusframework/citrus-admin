import {Injectable} from "@angular/core";
import {Actions} from "@ngrx/effects";
import {Observable} from "rxjs";
import {Action} from "@ngrx/store";
export interface IAsyncActionTypes {
    FETCH:string,
    SUCCESS:string;
    FAILED:string;
}

export const AsyncActionType = (name:string):IAsyncActionTypes => ({
    FETCH: `${name}.FETCH`,
    SUCCESS: `${name}.SUCCESS`,
    FAILED: `${name}.FAILED`,
})


@Injectable()
export class AsyncActions {
    constructor(private action:Actions) {}

    handleEffect(type:IAsyncActionTypes, cb:(a:Action)=>Observable<any>) {
        return this.action.ofType(type.FETCH)
            .switchMap(a => cb(a).map(payload => ({type:type.SUCCESS, payload})))
    }
}

export type Partial<T> = {
    [P in keyof T]?: T[P]
}

export const assign = <T>(base:T, extend:Partial<T>) => {
    return Object.assign({}, base, extend);
}

export class Extender<T> {
    constructor(private base:T) {}

    with(e:Partial<T>) {
        return assign(this.base, e);
    }
}