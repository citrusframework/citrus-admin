import {NgModule} from "@angular/core";
import {TruncatePipe} from "./truncate.pipe";
import {ClazzPipe} from "./clazz.pipe";
import {AsyncActions} from "./redux.util";

@NgModule({
    providers: [
        AsyncActions
    ],
    declarations: [
        TruncatePipe,
        ClazzPipe
    ],
    exports: [
        TruncatePipe,
        ClazzPipe
    ]
})
export class UtilModule {}