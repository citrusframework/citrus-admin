import {Pipe, PipeTransform} from '@angular/core'

@Pipe({
    name: 'clazz'
})
export class ClazzPipe implements PipeTransform {
    transform(value: string, ...args: any[]) : string {
        let target = args.length > 0 ? args[0] : 'name';

        if (target == 'package') {
            return value.substring(0, value.lastIndexOf('.'));
        } else if (target == 'name') {
            return value.substring(value.lastIndexOf('.') + 1);
        }

        return value;
    }
}