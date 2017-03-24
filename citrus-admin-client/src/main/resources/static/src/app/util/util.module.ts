import {NgModule} from "@angular/core";
import {TruncatePipe} from "./truncate.pipe";
import {ClazzPipe} from "./clazz.pipe";
import {AsyncActions} from "./redux.util";
import {LogPipe} from "./log.pipe";

@NgModule({
    providers: [
        AsyncActions
    ],
    declarations: [
        TruncatePipe,
        ClazzPipe,
        LogPipe
    ],
    exports: [
        TruncatePipe,
        ClazzPipe,
        LogPipe
    ]
})
export class UtilModule {}