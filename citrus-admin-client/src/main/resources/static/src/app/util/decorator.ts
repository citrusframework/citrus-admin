import {Observable} from "rxjs";

export type ObservableProducer = {(...args:any[]):Observable<any>};


/**
 * WIP consider to use _.memoize
 * @returns {(target:Object, propertyKey:string, descriptor:TypedPropertyDescriptor<ObservableProducer>)=>undefined}
 * @constructor
 */
export function Cached():MethodDecorator {
    return function CacheDecorator(
        target:Object,
        propertyKey:string,
        descriptor:TypedPropertyDescriptor<ObservableProducer>
    ) {
        const ov = descriptor.value;
        descriptor.value = function(...args:any[]) {
            return ov.apply(this, args);
        }
    }
}