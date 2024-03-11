/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import TransactionDetailComponent from '@/entities/transaction/transaction-details.vue';
import TransactionClass from '@/entities/transaction/transaction-details.component';
import TransactionService from '@/entities/transaction/transaction.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Transaction Management Detail Component', () => {
    let wrapper: Wrapper<TransactionClass>;
    let comp: TransactionClass;
    let transactionServiceStub: SinonStubbedInstance<TransactionService>;

    beforeEach(() => {
      transactionServiceStub = sinon.createStubInstance<TransactionService>(TransactionService);

      wrapper = shallowMount<TransactionClass>(TransactionDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { transactionService: () => transactionServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundTransaction = { id: 123 };
        transactionServiceStub.find.resolves(foundTransaction);

        // WHEN
        comp.retrieveTransaction(123);
        await comp.$nextTick();

        // THEN
        expect(comp.transaction).toBe(foundTransaction);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundTransaction = { id: 123 };
        transactionServiceStub.find.resolves(foundTransaction);

        // WHEN
        comp.beforeRouteEnter({ params: { transactionId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.transaction).toBe(foundTransaction);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
