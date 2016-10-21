import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule }   from '@angular/forms';

import { SetupComponent } from './components/setup.component'

@NgModule({
    imports: [
        BrowserModule, FormsModule
    ],
    declarations: [SetupComponent],
    bootstrap: [SetupComponent]
})
export class SetupModule {
}
