import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { SetupModule } from './setup.module';
const platform = platformBrowserDynamic();
platform.bootstrapModule(SetupModule);
