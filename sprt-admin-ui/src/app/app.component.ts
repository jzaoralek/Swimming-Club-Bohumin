import { Component } from '@angular/core';
import { CustConfigService } from './service/cust-config-service.service';
import {Observable,of, from } from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Sportologic';

  constructor( 
    private custConfigService: CustConfigService) {
  }
 
  healthCheck() {
    this.custConfigService.healtCheck().subscribe(response => console.log(response));
 }

}
