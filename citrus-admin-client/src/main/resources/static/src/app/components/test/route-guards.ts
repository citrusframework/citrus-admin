import {CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate, Router} from "@angular/router";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {TestStateService} from "./test.state";

@Injectable()
export class CanActivateTestTab implements CanActivateChild {

    constructor(
        private testState:TestStateService,
        private router:Router
    ) {}

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        const testSelected = this.testState.selectedTest.map(t => t != null);
        testSelected.filter(s => !s).subscribe(() => this.router.navigate(['tests']))
        return testSelected;
    }

}

@Injectable()
export class CannotActivate implements CanActivate {
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        return false;
    }


}