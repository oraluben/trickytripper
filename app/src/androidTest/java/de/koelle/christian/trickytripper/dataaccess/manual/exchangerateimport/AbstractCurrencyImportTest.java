package de.koelle.christian.trickytripper.dataaccess.manual.exchangerateimport;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Before;

import java.util.LinkedHashSet;
import java.util.Set;

import de.koelle.christian.trickytripper.exchangerates.impl.AsyncExchangeRateJsonResolverFccaImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterImpl;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultCallback;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateImporterResultContainer;
import de.koelle.christian.trickytripper.exchangerates.impl.ExchangeRateResultExtractorJsonGoogleImpl;

public abstract class AbstractCurrencyImportTest  {

    private Set<ExchangeRateImporterResultContainer> resultCollector;
    private ExchangeRateImporterImpl importer;
    protected Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
        importer = new ExchangeRateImporterImpl();
        importer.setAsyncExchangeRateResolver(new AsyncExchangeRateJsonResolverFccaImpl(context));
        importer.setExchangeRateResultExtractor(new ExchangeRateResultExtractorJsonGoogleImpl());
        importer.setChunkDelay(2000);
        /* ================= Desire SIM-connection ========================= */
        /* ResponseTimes: [2829, 171,173, 162, 162, 203, 151, 158, 205, 157] */
        /* 5000@70 fails */
        /* 2000@70 fails */
        /* 2000@50 works */
        /* ================= Desire WLAN ================================== */
        /* ResponseTimes: [470, 80, 85, 113, 99, 104, 81, 101, 159, 100] */
        /* 2000@100 Timeout waiting for connection */
        /* 1000@100 Timeout waiting for connection */
        /* 2000@85 Timeout waiting for connection */
        /* 2000@70 works */
        /* 1000@50 works */
        /* 1000@350 Timeout waiting for connection */
        /* ================= EMU ================================== */
        /* ResponseTimes: [551, 181, 134, 170, 229, 169, 169, 98, 169, 236] */
        /* 2000@350 works, 400 -> Timeout waiting for connection */
        /* 1000@350 works, 400 -> Timeout waiting for connection */
        /* 0@350 works, 400 -> Timeout waiting for connection */
        importer.setChunkSize(50);

        resetResultFields();
    }

    @SuppressWarnings("static-access")
    protected void waitForResult(int maxIterations, int runIterations) {
        if (runIterations < maxIterations) {
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void resetResultFields() {
        resultCollector = new LinkedHashSet<>();
    }

    protected final class ResultCollectingExchangeRateImporterResultCallback implements
            ExchangeRateImporterResultCallback {

        public void deliverResult(ExchangeRateImporterResultContainer resultContainer) {
            Assert.assertTrue("An exchange rate result returned from the importer was null result="
                            + resultContainer,
                    resultContainer.exchangeRateResult != null);
            boolean requestWasSuccess = resultContainer.requestWasSuccess();
            Assert.assertEquals(true, requestWasSuccess);
            System.out.println("success=" + requestWasSuccess);

            resultCollector.add(resultContainer);
        }
    }


    protected Set<ExchangeRateImporterResultContainer> getResultCollector() {
        return resultCollector;
    }

    protected ExchangeRateImporterImpl getImporter() {
        return importer;
    }

}
