import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-cust-config-result-form',
  templateUrl: './cust-config-result-form.component.html',
  styleUrls: ['./cust-config-result-form.component.css']
})
export class CustConfigResultFormComponent implements OnInit {

  data: any = {};
  routeState: any;

  constructor(private route: ActivatedRoute, 
              private router: Router) {
          if (this.router.getCurrentNavigation()?.extras.state) {
              
            this.routeState = this.router.getCurrentNavigation()?.extras.state;
            if (this.routeState) {
                  this.data.newCustData = this.routeState.newCustData ? JSON.parse(this.routeState.newCustData) : '';
              }
          }
    }

  ngOnInit(): void {

  }

}
