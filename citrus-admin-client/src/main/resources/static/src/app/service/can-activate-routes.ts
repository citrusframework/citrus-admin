import {Injectable} from "@angular/core";
import {CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router} from "@angular/router";
import {Observable} from "rxjs";
import 'rxjs/add/operator/catch'
import {ProjectService} from "./project.service";
@Injectable()
export class CanActivateRoutes implements CanActivate {
    private setupRoute = '/setup';

    constructor(
        private projectService:ProjectService,
        private router:Router
    ) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean>|Promise<boolean>|boolean {
        const guard$ = this.projectService.getActiveProject().map(() => true).catch((e) => Observable.of(false));
        guard$.filter(b => !b).subscribe(() => this.router.navigate([this.setupRoute]));
        return guard$;
    }

}