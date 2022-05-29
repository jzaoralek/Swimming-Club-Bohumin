import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { CustConfigFormComponent } from './cust-config-form/cust-config-form.component';
import { CustConfigResultFormComponent } from './cust-config-result-form/cust-config-result-form.component';
import { CustConfigService } from './service/cust-config-service.service';
import {GlobalErrorHandlerService} from './service/global-error-handler-service';
import { RECAPTCHA_SETTINGS, RecaptchaFormsModule, RecaptchaModule, RecaptchaSettings } from 'ng-recaptcha';
import { environment } from '../environments/environment';


@NgModule({
  declarations: [
    AppComponent,
    CustConfigFormComponent,
    CustConfigResultFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    RecaptchaModule,
    RecaptchaFormsModule,
  ],
  providers: [
    CustConfigService, 
    GlobalErrorHandlerService,
    {
      provide: RECAPTCHA_SETTINGS,
      useValue: {
        siteKey: environment.recaptcha.siteKey,
      } as RecaptchaSettings,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }