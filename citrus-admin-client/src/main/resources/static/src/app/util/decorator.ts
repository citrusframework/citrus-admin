import {Observable} from "rxjs";
import * as _ from 'lodash';

export type ObservableProducer = {(...args:any[]):Observable<any>};


export function memoize() {
    return function (target:Object, key:string, desc:PropertyDescriptor) {
        const v = desc.value;
        desc.value = _.memoize(v);
        return desc;
    }
}

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

interface LogDecoratorConfig {
    trace?:boolean;
}
const defaultConf:LogDecoratorConfig = {
    trace: false
}
export function Log(config:LogDecoratorConfig = defaultConf) {
    console.log('Log')
    return function(...args:any[]) {
        if(args.length === 3) {
            return logMethodFactory(config)
        }
    }
}

export function Trace() {
    console.log('Trace')
    return Log(Object.assign({}, defaultConf, {trace: true}))
}

const logMethodFactory = (config:LogDecoratorConfig):MethodDecorator => {
    const logger = config.trace ? console.trace : console.log;
    console.log('Decorate', config, logger)
    return function(
        target:Function,
        propertyKey:string,
        descriptor:PropertyDescriptor
    ) {
        const value = descriptor.value;
        descriptor.value = function(...args:any[]) {
            logger(`${target.name}.${propertyKey}()`, args)
            const result = value.apply(this, args);
            logger(`Result =>`, result)
            return result;
        }
    }
}