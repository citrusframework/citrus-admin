import {NgModule} from "@angular/core";
import {TruncatePipe} from "./truncate.pipe";
import {ClazzPipe} from "./clazz.pipe";
import {AsyncActions} from "./redux.util";
import {LogPipe} from "./log.pipe";
import {PrettyPrintPipe} from "./prettyprint.pipe";

@NgModule({
    providers: [
        AsyncActions
    ],
    declarations: [
        TruncatePipe,
        ClazzPipe,
        LogPipe,
        PrettyPrintPipe
    ],
    exports: [
        TruncatePipe,
        ClazzPipe,
        LogPipe,
        PrettyPrintPipe
    ]
})
export class UtilModule {}
