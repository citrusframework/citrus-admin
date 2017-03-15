import {Injectable} from "@angular/core";
import {AppState} from "../state.module";
import {Store} from "@ngrx/store";
import {Observable} from "rxjs";

@Injectable()
export class RouterState {

    private _path:Observable<string>;

    constructor(
        private store:Store<AppState>
    ) {
        this._path = this.store.select(s => s.router.path)

    }

    get path() {
        return this._path;
    }

}