package de.laboranowitsch.priceimporter.service;

import de.laboranowitsch.priceimporter.PriceImporterApplication;
import de.laboranowitsch.priceimporter.domain.*;
import de.laboranowitsch.priceimporter.util.dbloader.DbLoader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Tests for {@link RecordImportService}
 * for {@link de.laboranowitsch.priceimporter.domain.CompositeRecord}.
 *
 * Created by cla on 5/06/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PriceImporterApplication.class)
public class RecordImportServiceTests {

    private static final Logger LOG = LoggerFactory.getLogger(RecordImportServiceTests.class);
    public static final String TRADE = "TRADE";
    public static final String PD = "PD";

    @Autowired
    private DbLoader dbLoader;
    @Autowired
    private RecordImportService recordImportService;
    @Autowired
    private DataSource dataSource;

    @Before
    public void before() throws SQLException {
        dbLoader.prepareDatabase();
    }
    @Test
    public void testContextLoads() {
        assertThat("wiring of dependencies is done properly", recordImportService, is(not(nullValue())));
    }


    @Transactional
    @Test
    public void testImportRecord() {
        CompositeRecord compositeRecord = createCompositeRecord(TRADE, 23.45, 567.89);
        recordImportService.importRecord(compositeRecord);
        List<FactData> factData = getFactData();
        assertThat("one entry has been inserted", factData.size(), is(equalTo(1)));

        FactData factDataRecord = factData.get(0);
        LOG.info("Fact data record: {}", factDataRecord);

        assertThat("contains sequence value 1L", factDataRecord.getId(), is(equalTo(1L)));
        assertThat("contains the right totalDemand", factDataRecord.getTotalDemand(), is(closeTo(567.89, 0.0001)));
        assertThat("contains the right rpr", factDataRecord.getRpr(), is(closeTo(23.45, 0.0001)));
        assertThat("contains sequence value 1L", factDataRecord.getDateTimeId(), is(equalTo(1L)));
        assertThat("contains sequence value 1L", factDataRecord.getPeriodId(), is(equalTo(1L)));
        assertThat("contains sequence value 1L", factDataRecord.getRegionId(), is(equalTo(1L)));
    }

    @Transactional
    @Test
    public void testUpdateRecord() {
        CompositeRecord compositeRecord = createCompositeRecord(TRADE, 23.45, 567.89);
        recordImportService.importRecord(compositeRecord);
        compositeRecord = createCompositeRecord(TRADE, 17.89, 1000.56);
        recordImportService.importRecord(compositeRecord);
        List<FactData> factData = getFactData();
        assertThat("one entry has been inserted", factData.size(), is(equalTo(1)));

        FactData factDataRecord = factData.get(0);
        LOG.info("Fact data record: {}", factDataRecord);

        assertThat("contains sequence value 1L", factDataRecord.getId(), is(equalTo(1L)));
        assertThat("contains the right totalDemand", factDataRecord.getTotalDemand(), is(closeTo(1000.56, 0.0001)));
        assertThat("contains the right rpr", factDataRecord.getRpr(), is(closeTo(17.89, 0.0001)));
        assertThat("contains sequence value 1L", factDataRecord.getDateTimeId(), is(equalTo(1L)));
        assertThat("contains sequence value 1L", factDataRecord.getPeriodId(), is(equalTo(1L)));
        assertThat("contains sequence value 1L", factDataRecord.getRegionId(), is(equalTo(1L)));
    }

    @Transactional
    @Test
    public void testImportSecondRecord() {
        CompositeRecord compositeRecord = createCompositeRecord(TRADE, 23.45, 567.89);
        recordImportService.importRecord(compositeRecord);
        compositeRecord = createCompositeRecord(PD, 17.89, 1000.67);
        recordImportService.importRecord(compositeRecord);

        List<FactData> factData = getFactData();
        assertThat("one entry has been inserted", factData.size(), is(equalTo(2)));

        FactData factDataRecord = factData.get(1);
        LOG.info("Fact data record: {}", factDataRecord);

        assertThat("contains sequence value 2L", factDataRecord.getId(), is(equalTo(2L)));
        assertThat("contains the right totalDemand", factDataRecord.getTotalDemand(), is(closeTo(1000.67, 0.0001)));
        assertThat("contains the right rpr", factDataRecord.getRpr(), is(closeTo(17.89, 0.0001)));
        assertThat("contains sequence value 1L", factDataRecord.getDateTimeId(), is(equalTo(1L)));
        assertThat("contains sequence value 2L", factDataRecord.getPeriodId(), is(equalTo(2L)));
        assertThat("contains sequence value 1L", factDataRecord.getRegionId(), is(equalTo(1L)));
    }

    private List<FactData> getFactData() {
        NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate.query("select * from int_test_f_energy_price_demand", ((rs, rowNum) -> FactData.builder().id(rs.getLong(1))
                .totalDemand(rs.getDouble(2))
                .rpr(rs.getDouble(3))
                .regionId(rs.getLong(4))
                .periodId(rs.getLong(5))
                .dateTimeId(rs.getLong(6))
                .build()));
    }

    private CompositeRecord createCompositeRecord(String periodType, Double rpr, Double totalDemand) {
        CompositeRecord compositeRecord = new CompositeRecord();
        compositeRecord.setDateTime(DateTime.builder().dayOfMonth(1)
                .monthOfYear(1)
                .year(2016)
                .hourOfDay(00)
                .minuteOfHour(00)
                .build());
        compositeRecord.setPeriod(Period.builder().period(periodType).build());
        compositeRecord.setRegion(Region.builder().region("NSW").build());
        compositeRecord.setFactData(FactData.builder().rpr(rpr).totalDemand(totalDemand).build());
        return compositeRecord;
    }
}
