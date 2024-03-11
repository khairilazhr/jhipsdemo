package com.jhipsdemo.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jhipsdemo.IntegrationTest;
import com.jhipsdemo.domain.Transaction;
import com.jhipsdemo.repository.TransactionRepository;
import com.jhipsdemo.service.criteria.TransactionCriteria;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TransactionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_TOTALPRICE = 1;
    private static final Integer UPDATED_TOTALPRICE = 2;
    private static final Integer SMALLER_TOTALPRICE = 1 - 1;

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());
    private static final LocalDate SMALLER_DATE = LocalDate.ofEpochDay(-1L);

    private static final String DEFAULT_GENDER = "AAAAAAAAAA";
    private static final String UPDATED_GENDER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transactions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTransactionMockMvc;

    private Transaction transaction;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .name(DEFAULT_NAME)
            .title(DEFAULT_TITLE)
            .totalprice(DEFAULT_TOTALPRICE)
            .date(DEFAULT_DATE)
            .gender(DEFAULT_GENDER);
        return transaction;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transaction createUpdatedEntity(EntityManager em) {
        Transaction transaction = new Transaction()
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .totalprice(UPDATED_TOTALPRICE)
            .date(UPDATED_DATE)
            .gender(UPDATED_GENDER);
        return transaction;
    }

    @BeforeEach
    public void initTest() {
        transaction = createEntity(em);
    }

    @Test
    @Transactional
    void createTransaction() throws Exception {
        int databaseSizeBeforeCreate = transactionRepository.findAll().size();
        // Create the Transaction
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isCreated());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate + 1);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTransaction.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testTransaction.getTotalprice()).isEqualTo(DEFAULT_TOTALPRICE);
        assertThat(testTransaction.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTransaction.getGender()).isEqualTo(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    void createTransactionWithExistingId() throws Exception {
        // Create the Transaction with an existing ID
        transaction.setId(1L);

        int databaseSizeBeforeCreate = transactionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setName(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = transactionRepository.findAll().size();
        // set the field null
        transaction.setTitle(null);

        // Create the Transaction, which fails.

        restTransactionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isBadRequest());

        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTransactions() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].totalprice").value(hasItem(DEFAULT_TOTALPRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)));
    }

    @Test
    @Transactional
    void getTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get the transaction
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL_ID, transaction.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transaction.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.totalprice").value(DEFAULT_TOTALPRICE))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER));
    }

    @Test
    @Transactional
    void getTransactionsByIdFiltering() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        Long id = transaction.getId();

        defaultTransactionShouldBeFound("id.equals=" + id);
        defaultTransactionShouldNotBeFound("id.notEquals=" + id);

        defaultTransactionShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.greaterThan=" + id);

        defaultTransactionShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTransactionShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTransactionsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name equals to DEFAULT_NAME
        defaultTransactionShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the transactionList where name equals to UPDATED_NAME
        defaultTransactionShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionsByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name not equals to DEFAULT_NAME
        defaultTransactionShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the transactionList where name not equals to UPDATED_NAME
        defaultTransactionShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTransactionShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the transactionList where name equals to UPDATED_NAME
        defaultTransactionShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name is not null
        defaultTransactionShouldBeFound("name.specified=true");

        // Get all the transactionList where name is null
        defaultTransactionShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByNameContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name contains DEFAULT_NAME
        defaultTransactionShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the transactionList where name contains UPDATED_NAME
        defaultTransactionShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionsByNameNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where name does not contain DEFAULT_NAME
        defaultTransactionShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the transactionList where name does not contain UPDATED_NAME
        defaultTransactionShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title equals to DEFAULT_TITLE
        defaultTransactionShouldBeFound("title.equals=" + DEFAULT_TITLE);

        // Get all the transactionList where title equals to UPDATED_TITLE
        defaultTransactionShouldNotBeFound("title.equals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title not equals to DEFAULT_TITLE
        defaultTransactionShouldNotBeFound("title.notEquals=" + DEFAULT_TITLE);

        // Get all the transactionList where title not equals to UPDATED_TITLE
        defaultTransactionShouldBeFound("title.notEquals=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title in DEFAULT_TITLE or UPDATED_TITLE
        defaultTransactionShouldBeFound("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE);

        // Get all the transactionList where title equals to UPDATED_TITLE
        defaultTransactionShouldNotBeFound("title.in=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title is not null
        defaultTransactionShouldBeFound("title.specified=true");

        // Get all the transactionList where title is null
        defaultTransactionShouldNotBeFound("title.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title contains DEFAULT_TITLE
        defaultTransactionShouldBeFound("title.contains=" + DEFAULT_TITLE);

        // Get all the transactionList where title contains UPDATED_TITLE
        defaultTransactionShouldNotBeFound("title.contains=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTitleNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where title does not contain DEFAULT_TITLE
        defaultTransactionShouldNotBeFound("title.doesNotContain=" + DEFAULT_TITLE);

        // Get all the transactionList where title does not contain UPDATED_TITLE
        defaultTransactionShouldBeFound("title.doesNotContain=" + UPDATED_TITLE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice equals to DEFAULT_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.equals=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice equals to UPDATED_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.equals=" + UPDATED_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice not equals to DEFAULT_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.notEquals=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice not equals to UPDATED_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.notEquals=" + UPDATED_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice in DEFAULT_TOTALPRICE or UPDATED_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.in=" + DEFAULT_TOTALPRICE + "," + UPDATED_TOTALPRICE);

        // Get all the transactionList where totalprice equals to UPDATED_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.in=" + UPDATED_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice is not null
        defaultTransactionShouldBeFound("totalprice.specified=true");

        // Get all the transactionList where totalprice is null
        defaultTransactionShouldNotBeFound("totalprice.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice is greater than or equal to DEFAULT_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.greaterThanOrEqual=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice is greater than or equal to UPDATED_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.greaterThanOrEqual=" + UPDATED_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice is less than or equal to DEFAULT_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.lessThanOrEqual=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice is less than or equal to SMALLER_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.lessThanOrEqual=" + SMALLER_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice is less than DEFAULT_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.lessThan=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice is less than UPDATED_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.lessThan=" + UPDATED_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByTotalpriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where totalprice is greater than DEFAULT_TOTALPRICE
        defaultTransactionShouldNotBeFound("totalprice.greaterThan=" + DEFAULT_TOTALPRICE);

        // Get all the transactionList where totalprice is greater than SMALLER_TOTALPRICE
        defaultTransactionShouldBeFound("totalprice.greaterThan=" + SMALLER_TOTALPRICE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date equals to DEFAULT_DATE
        defaultTransactionShouldBeFound("date.equals=" + DEFAULT_DATE);

        // Get all the transactionList where date equals to UPDATED_DATE
        defaultTransactionShouldNotBeFound("date.equals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date not equals to DEFAULT_DATE
        defaultTransactionShouldNotBeFound("date.notEquals=" + DEFAULT_DATE);

        // Get all the transactionList where date not equals to UPDATED_DATE
        defaultTransactionShouldBeFound("date.notEquals=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date in DEFAULT_DATE or UPDATED_DATE
        defaultTransactionShouldBeFound("date.in=" + DEFAULT_DATE + "," + UPDATED_DATE);

        // Get all the transactionList where date equals to UPDATED_DATE
        defaultTransactionShouldNotBeFound("date.in=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date is not null
        defaultTransactionShouldBeFound("date.specified=true");

        // Get all the transactionList where date is null
        defaultTransactionShouldNotBeFound("date.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date is greater than or equal to DEFAULT_DATE
        defaultTransactionShouldBeFound("date.greaterThanOrEqual=" + DEFAULT_DATE);

        // Get all the transactionList where date is greater than or equal to UPDATED_DATE
        defaultTransactionShouldNotBeFound("date.greaterThanOrEqual=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date is less than or equal to DEFAULT_DATE
        defaultTransactionShouldBeFound("date.lessThanOrEqual=" + DEFAULT_DATE);

        // Get all the transactionList where date is less than or equal to SMALLER_DATE
        defaultTransactionShouldNotBeFound("date.lessThanOrEqual=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsLessThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date is less than DEFAULT_DATE
        defaultTransactionShouldNotBeFound("date.lessThan=" + DEFAULT_DATE);

        // Get all the transactionList where date is less than UPDATED_DATE
        defaultTransactionShouldBeFound("date.lessThan=" + UPDATED_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByDateIsGreaterThanSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where date is greater than DEFAULT_DATE
        defaultTransactionShouldNotBeFound("date.greaterThan=" + DEFAULT_DATE);

        // Get all the transactionList where date is greater than SMALLER_DATE
        defaultTransactionShouldBeFound("date.greaterThan=" + SMALLER_DATE);
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderIsEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender equals to DEFAULT_GENDER
        defaultTransactionShouldBeFound("gender.equals=" + DEFAULT_GENDER);

        // Get all the transactionList where gender equals to UPDATED_GENDER
        defaultTransactionShouldNotBeFound("gender.equals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderIsNotEqualToSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender not equals to DEFAULT_GENDER
        defaultTransactionShouldNotBeFound("gender.notEquals=" + DEFAULT_GENDER);

        // Get all the transactionList where gender not equals to UPDATED_GENDER
        defaultTransactionShouldBeFound("gender.notEquals=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderIsInShouldWork() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender in DEFAULT_GENDER or UPDATED_GENDER
        defaultTransactionShouldBeFound("gender.in=" + DEFAULT_GENDER + "," + UPDATED_GENDER);

        // Get all the transactionList where gender equals to UPDATED_GENDER
        defaultTransactionShouldNotBeFound("gender.in=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderIsNullOrNotNull() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender is not null
        defaultTransactionShouldBeFound("gender.specified=true");

        // Get all the transactionList where gender is null
        defaultTransactionShouldNotBeFound("gender.specified=false");
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender contains DEFAULT_GENDER
        defaultTransactionShouldBeFound("gender.contains=" + DEFAULT_GENDER);

        // Get all the transactionList where gender contains UPDATED_GENDER
        defaultTransactionShouldNotBeFound("gender.contains=" + UPDATED_GENDER);
    }

    @Test
    @Transactional
    void getAllTransactionsByGenderNotContainsSomething() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        // Get all the transactionList where gender does not contain DEFAULT_GENDER
        defaultTransactionShouldNotBeFound("gender.doesNotContain=" + DEFAULT_GENDER);

        // Get all the transactionList where gender does not contain UPDATED_GENDER
        defaultTransactionShouldBeFound("gender.doesNotContain=" + UPDATED_GENDER);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTransactionShouldBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transaction.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].totalprice").value(hasItem(DEFAULT_TOTALPRICE)))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER)));

        // Check, that the count call also returns 1
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTransactionShouldNotBeFound(String filter) throws Exception {
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTransactionMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingTransaction() throws Exception {
        // Get the transaction
        restTransactionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction
        Transaction updatedTransaction = transactionRepository.findById(transaction.getId()).get();
        // Disconnect from session so that the updates on updatedTransaction are not directly saved in db
        em.detach(updatedTransaction);
        updatedTransaction.name(UPDATED_NAME).title(UPDATED_TITLE).totalprice(UPDATED_TOTALPRICE).date(UPDATED_DATE).gender(UPDATED_GENDER);

        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransaction.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTransaction.getTotalprice()).isEqualTo(UPDATED_TOTALPRICE);
        assertThat(testTransaction.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTransaction.getGender()).isEqualTo(UPDATED_GENDER);
    }

    @Test
    @Transactional
    void putNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transaction.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transaction)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction.title(UPDATED_TITLE);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTransaction.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTransaction.getTotalprice()).isEqualTo(DEFAULT_TOTALPRICE);
        assertThat(testTransaction.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testTransaction.getGender()).isEqualTo(DEFAULT_GENDER);
    }

    @Test
    @Transactional
    void fullUpdateTransactionWithPatch() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();

        // Update the transaction using partial update
        Transaction partialUpdatedTransaction = new Transaction();
        partialUpdatedTransaction.setId(transaction.getId());

        partialUpdatedTransaction
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .totalprice(UPDATED_TOTALPRICE)
            .date(UPDATED_DATE)
            .gender(UPDATED_GENDER);

        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransaction))
            )
            .andExpect(status().isOk());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
        Transaction testTransaction = transactionList.get(transactionList.size() - 1);
        assertThat(testTransaction.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTransaction.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testTransaction.getTotalprice()).isEqualTo(UPDATED_TOTALPRICE);
        assertThat(testTransaction.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testTransaction.getGender()).isEqualTo(UPDATED_GENDER);
    }

    @Test
    @Transactional
    void patchNonExistingTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transaction.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isBadRequest());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTransaction() throws Exception {
        int databaseSizeBeforeUpdate = transactionRepository.findAll().size();
        transaction.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(transaction))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Transaction in the database
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTransaction() throws Exception {
        // Initialize the database
        transactionRepository.saveAndFlush(transaction);

        int databaseSizeBeforeDelete = transactionRepository.findAll().size();

        // Delete the transaction
        restTransactionMockMvc
            .perform(delete(ENTITY_API_URL_ID, transaction.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Transaction> transactionList = transactionRepository.findAll();
        assertThat(transactionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
