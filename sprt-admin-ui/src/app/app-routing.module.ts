import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CustConfigFormComponent } from './cust-config-form/cust-config-form.component';
import { CustConfigResultFormComponent } from './cust-config-result-form/cust-config-result-form.component';

const routes: Routes = [
  { path: 'create-instance', component: CustConfigFormComponent },
  { path: 'cust-config-result', component: CustConfigResultFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }