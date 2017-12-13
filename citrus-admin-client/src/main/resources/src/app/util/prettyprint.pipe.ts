import {Pipe, PipeTransform} from '@angular/core'
import * as prettyData from 'pretty-data'

@Pipe({
    name: 'prettyprint'
})
export class PrettyPrintPipe implements PipeTransform {
    transform(value: string, ...args: any[]) : string {
        let type = args.length > 0 ? args[0] : 'xml';

        if (type == 'xml') {
            return prettyData.pd.xml(value);
        } else if (type == 'json') {
            return prettyData.pd.json(value);
        } else if (type == 'css') {
            return prettyData.pd.css(value);
        }

        return value;
    }
}
