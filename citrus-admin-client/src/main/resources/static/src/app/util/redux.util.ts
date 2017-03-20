import {Injectable} from "@angular/core";
import {Actions} from "@ngrx/effects";
import {Observable} from "rxjs";
import {Action as ReduxAction} from "@ngrx/store";
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

export const AsyncActionType = (name:string):IAsyncActionTypes => ({
    FETCH: `${name}.FETCH`,
    SUCCESS: `${name}.SUCCESS`,
    FAILED: `${name}.FAILED`,
})

export interface ICrudActionTypes<T> {
    CREATE:T;
    READ:T;
    UPDATE:T;
    DELETE:T;
}

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
})

@Injectable()
export class AsyncActions {
    constructor(private action:Actions) {}

    handleEffect<T>(type:IAsyncActionTypes, cb:(a:Action<T>)=>Observable<T>) {
        return this.action.ofType(type.FETCH)
            .switchMap(a => cb(a).map((payload:T) => ({type:type.SUCCESS, payload})))
    }
}

export type IdMap<T> = {[id:string]:T};
export const toIdMap = <T>(list:T[], getId:(e:T)=>string) => list.reduce((idm, e) => ({...idm, [getId(e)]:e}), {} as IdMap<T>);
export const toArray = <T>(idMap:IdMap<T>) => Object.keys(idMap).map(k => idMap[k])

export const deleteFromMap = <T>(map:IdMap<T>, key:string) => {
    return Object.keys(map).filter(k => k !== key).reduce((no, k) => ({...no, [k]:map[k]}), {})
}