import {NgModule} from "@angular/core";
import {AlertService} from "./alert.service";
import {CanActivateRoutes} from "./can-activate-routes";
import {ConfigService} from "./config.service";
import {EndpointService} from "./endpoint.service";
import {ProjectService} from "./project.service";
import {ProjectSetupService} from "./project.setup.service";
import {ReportService} from "./report.service";
import {SpringBeanService} from "./springbean.service";
import {TestService} from "./test.service";
import {RouterState} from "./router-state.service";
@NgModule({
    providers: [
        AlertService,
        CanActivateRoutes,
        ConfigService,
        EndpointService,
        ProjectService,
        ProjectSetupService,
        ReportService,
        SpringBeanService,
        TestService,
        RouterState
    ]
})
export class ServiceModule {}