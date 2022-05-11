import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppComponent } from './app.component';
import { CustConfigFormComponent } from './cust-config-form/cust-config-form.component';
import { CustConfigService } from './service/cust-config-service.service';

@NgModule({
  declarations: [
    AppComponent,
    CustConfigFormComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [CustConfigService],
  bootstrap: [AppComponent]
})
export class AppModule { }