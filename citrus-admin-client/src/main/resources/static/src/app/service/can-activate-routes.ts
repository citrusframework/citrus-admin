import {Injectable} from "@angular/core";
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot} from "@angular/router";
import {Observable} from "rxjs";
import 'rxjs/add/operator/catch'
import {ProjectService} from "./project.service";
@Injectable()
export class CanActivateRoutes implements CanActivate {

    constructor(
        private projectService:ProjectService
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        return this.projectService.getActiveProject().map(() => true).catch((e) => Observable.of(false));
    }

}