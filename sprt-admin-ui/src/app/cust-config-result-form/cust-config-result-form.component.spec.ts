import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CustConfigResultFormComponent } from './cust-config-result-form.component';

describe('CustConfigResultFormComponent', () => {
  let component: CustConfigResultFormComponent;
  let fixture: ComponentFixture<CustConfigResultFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CustConfigResultFormComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CustConfigResultFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
