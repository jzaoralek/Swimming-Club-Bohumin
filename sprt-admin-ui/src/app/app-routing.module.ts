import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CustConfigFormComponent } from './cust-config-form/cust-config-form.component';

const routes: Routes = [
  { path: 'create-instance', component: CustConfigFormComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }