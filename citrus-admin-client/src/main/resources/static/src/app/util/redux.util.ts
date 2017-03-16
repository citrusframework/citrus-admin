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

type object = {};

export type IdMap<T> = {[id:string]:T};
export const toIdMap = <T>(list:T[], getId:(e:T)=>string) => list.reduce((idm, e) => ({...idm, [getId(e)]:e}), {} as IdMap<T>);
export const toArray = <T>(idMap:IdMap<T>) => Object.keys(idMap).map(k => idMap[k])

export type Partial<T extends {}> = {
    [P in keyof T]?: T[P]
}

export const assign = <T>(base:T, extend:Partial<T>) => {
    return Object.assign({}, base, extend);
}

export class Extender<T> {
    constructor(private base:T) {}

    extendAndGet(e:Partial<T> = {} as Partial<T>) {
        return assign(this.base, e);
    }

    extend(e:Partial<T>) {
        this.base = this.extendAndGet(e);
        return this;
    }

    asExtender(getter:<O>(c:T)=>O) {
        return new Extender(getter(this.extendAndGet()));
    }
}