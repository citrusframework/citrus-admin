import {Injectable} from "@angular/core";
import {Actions} from "@ngrx/effects";
import {Observable} from "rxjs";
import {Action as ReduxAction} from "@ngrx/store";
import {AlertService} from "../service/alert.service";
import {Alert} from "../model/alert";
export interface IAsyncActionTypes {
    FETCH:string,
    SUCCESS:string;
    FAILED:string;
}

export interface Action<T> extends ReduxAction {
    payload?:T;
}

export const CreateAction = <T>(type:string, payload?:T) => ({type,payload});
export const CreateVoidAction = (type:string) => CreateAction<void>(type);

const AsyncActionNamesCache:IdMap<boolean> = {};
export const AsyncActionType = (name:string):IAsyncActionTypes => {
    if(AsyncActionNamesCache[name]) {
        throw new Error(`AsyncActionType: '${name}' is already defined`)
    }
    AsyncActionNamesCache[name] = true;
    return {
        FETCH: `${name}.FETCH`,
        SUCCESS: `${name}.SUCCESS`,
        FAILED: `${name}.FAILED`,
    }
};

export interface ICrudActionTypes<T> {
    CREATE:T;
    READ:T;
    UPDATE:T;
    DELETE:T;
}

export const ActionToString = (action:string, delimiter = '.') => action.split('.').map(a => a.toLowerCase()).join(' ');

export const CrudActionType = (name:string):ICrudActionTypes<string> => ({
    CREATE:`${name}.CREATE`,
    READ:`${name}.READ`,
    UPDATE:`${name}.UPDATE`,
    DELETE:`${name}.DELETE`
});

export const AsyncCrudActionType = (name:string):ICrudActionTypes<IAsyncActionTypes> => ({
    CREATE: AsyncActionType(`${name}.CREATE`),
    READ: AsyncActionType(`${name}.READ`),
    UPDATE: AsyncActionType(`${name}.UPDATE`),
    DELETE: AsyncActionType(`${name}.DELETE`)
});

@Injectable()
export class AsyncActions {
    constructor(
        private action:Actions,
        private alertService:AlertService
    ) {}

    handleEffect<T>(type:IAsyncActionTypes, cb:(a:Action<T>)=>Observable<T>) {
        return this.action.ofType(type.FETCH)
            .switchMap(a => cb(a).map((payload:T) => ({type:type.SUCCESS, payload})))
    }

    handleError<T>(type:IAsyncActionTypes|IAsyncActionTypes[], cb:(a:Action<T>)=>Alert) {
        const t = Array.isArray(type) ? type : [type];
        return this.action.ofType(...t.map(t => t.FAILED))
            .do(a => this.alertService.add(cb(a)))
    }

    handleSuccess<T>(type:IAsyncActionTypes|IAsyncActionTypes[], cb:(a:Action<T>)=>Alert) {
        const t = Array.isArray(type) ? type : [type];
        return this.action.ofType(...t.map(t => t.SUCCESS))
            .do(a => this.alertService.add(cb(a)))
    }
}

export type IdMap<T> = {[id:string]:T};
export const toIdMap = <T>(list:T[], getId:(e:T)=>string) => list.reduce((idm, e) => ({...idm, [getId(e)]:e}), {} as IdMap<T>);
export const toArray = <T>(idMap:IdMap<T>) => Object.keys(idMap).map(k => idMap[k]);

export const deleteFromMap = <T>(map:IdMap<T>, key:string) => {
    console.log(`Delete ${key}`, map);
    const r = Object.keys(map).filter(k => k !== key).reduce((no, k) => ({...no, [k]:map[k]}), {});
    console.log(`Deleted ${key}`, r);
    return r;
};

type ReducerCallback<T> = <A>(state:T,payload:A) => T;
type ActionReduceTupel<T> = [string[], ReducerCallback<T>]
export class ReducerBuilder<T extends {}> {

    constructor(private init:T) {}

    actionReducMap:ActionReduceTupel<T>[] = [];

    on(...actionsTypes:string[]) {
        return (reducer:ReducerCallback<T>) => {
            this.actionReducMap.push([actionsTypes, reducer]);
            return this;
        };
    }

    createReducer() {
        return (state:T = this.init, action:ReduxAction) => {
            return this.actionReducMap
                .filter(([actions]) => actions.indexOf(action.type) > -1)
                .reduce((s:T,[,r]) => ({...(s as any), ...(r(state, action.payload) as any)}), state)
        }
    }
}

export const notNull = () => <T>(o:T) => o != null;
export const log = (...args:any[]) => (...args2:any[]) => console.log.apply(console, [...args, ...args2])