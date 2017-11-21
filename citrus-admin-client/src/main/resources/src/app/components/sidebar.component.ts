import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ProjectService} from "../service/project.service";
import {AlertService} from "../service/alert.service";
import {Alert} from "../model/alert";

@Component({
    selector: 'sidebar',
    templateUrl: 'sidebar.html'
})
export class SidebarComponent {

    constructor(private router: Router,
                private projectService: ProjectService,
                private alertService: AlertService) {
    }

    navigate(url: string) {
        this.router.navigate([url]);
    }

    isActive(name: string) {
        return this.router.isActive(name, false);
    }

    close() {
        this.projectService.closeActiveProject()
          .subscribe(response => this.router.navigate(["/setup"]),
            error => this.notifyError(<any>error));
    }

  notifyError(error: any) {
    this.alertService.add(new Alert("danger", JSON.stringify(error), false));
  }
}
