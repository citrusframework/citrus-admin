import {CanActivateChild, ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate, Router} from "@angular/router";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";
import {TestStateService} from "./test.state";
import {CanActivateRoutes} from "../../service/can-activate-routes";

@Injectable()
export class CanActivateTestTabChild implements CanActivateChild {

    constructor(
        private testState:TestStateService,
        private router:Router
    ) {}

    canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        const testSelected = this.testState.selectedTest.map(t => t != null);
        testSelected.filter(s => !s).first().subscribe(() => this.router.navigate(['tests']))
        return testSelected;
    }

}

@Injectable()
export class CanActivateTestEditor implements CanActivate {

    constructor(
        private testState:TestStateService,
        private router:Router
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        const testSelected = this.testState.selectedTest;
        testSelected.filter(t => t != null).first().subscribe(t => this.router.navigate(['tests', 'editor', t.name]))
        return testSelected.map(_ => true);
    }

}

@Injectable()
export class CannotActivate implements CanActivate {
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        return false;
    }


}