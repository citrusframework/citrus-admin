import {Pipe, PipeTransform} from '@angular/core'

@Pipe({
    name: 'truncate'
})
export class TruncatePipe implements PipeTransform {
    transform(value: string, ...args: any[]) : string {
        let limit = args.length > 0 ? parseInt(args[0], 10) : 20;
        let trail = args.length > 1 ? args[1] : '...';

        return value.length > limit ? value.substring(0, limit) + trail : value;
    }
}