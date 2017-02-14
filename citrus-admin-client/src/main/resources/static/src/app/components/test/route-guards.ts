import {CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate} from "@angular/router";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {TestStateService} from "./test.state";

@Injectable()
export class CanActivateTestTab implements CanActivateChild {

    constructor(
        private testState:TestStateService
    ) {}

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        return this.testState.selectedTest.map(t => t != null)
    }

}

@Injectable()
export class CannotActivate implements CanActivate {
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        return false;
    }


}