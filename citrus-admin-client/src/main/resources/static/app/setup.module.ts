import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';

import { SetupComponent } from './components/setup.component'
import { ProjectSetupService } from './service/project.setup.service'

@NgModule({
    imports: [
        BrowserModule,
        HttpModule,
        FormsModule
    ],
    declarations: [SetupComponent],
    providers: [ProjectSetupService],
    bootstrap: [SetupComponent]
})
export class SetupModule {
}
