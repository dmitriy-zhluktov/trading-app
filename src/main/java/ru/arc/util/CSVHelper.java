package ru.arc.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import ru.arc.service.model.Pair;
import ru.arc.service.model.TradeHistory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class CSVHelper {
    public static ByteArrayInputStream toCSV(List<Pair<TradeHistory, TradeHistory>> tradeHistoryList) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            csvPrinter.printRecord(
                    List.of(
                            "Монета",
                            "Цена покупки",
                            "Цена продажи",
                            "Сумма покупки",
                            "Сумма продажи",
                            "Дата покупки",
                            "Дата продажи"
            ));
            for (Pair<TradeHistory, TradeHistory> item : tradeHistoryList) {
                List<String> data = Arrays.asList(
                        item.left.symbol,
                        item.left.execPrice.toString(),
                        item.right != null ? item.right.execPrice.toString() : "",
                        item.left.execValue.toString(),
                        item.right != null ? item.right.execValue.toString() : "",
                        item.left.execTime.toString(),
                        item.right != null ? item.right.execTime.toString() : ""
                        );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }
}
