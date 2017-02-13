import {NgModule} from "@angular/core";
import {TruncatePipe} from "./truncate.pipe";
import {AsyncAction} from "rxjs/scheduler/AsyncAction";
import {AsyncActions} from "./redux.util";
@NgModule({
    providers: [
        AsyncActions
    ],
    declarations: [
        TruncatePipe
    ],
    exports: [
        TruncatePipe
    ]
})
export class UtilModule {}