import {Pipe, PipeTransform} from '@angular/core'

@Pipe({
    name: 'log'
})
export class LogPipe implements PipeTransform {
    transform(value: string, ...args: any[]) : string {
        console.log(value, args);
        return value;
    }
}