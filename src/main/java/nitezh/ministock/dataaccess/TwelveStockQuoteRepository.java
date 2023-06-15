/*
 The MIT License

 Copyright (c) 2013 Nitesh Patel http://niteshpatel.github.io/ministocks

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.
 */

package nitezh.ministock.dataaccess;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import nitezh.ministock.R;
import nitezh.ministock.domain.StockQuote;
import nitezh.ministock.utils.Cache;
import nitezh.ministock.utils.UrlDataTools;


public class TwelveStockQuoteRepository {
    //    private static final String BASE_URL = "https://api.iextrading.com/1.0/stock/market/batch";
    private static final String BASE_URL = "https://api.twelvedata.com/quote";
    //"https://query1.finance.yahoo.com/v6/finance/quote?fields=symbol,regularMarketPrice,regularMarketChange,regularMarketChangePercent,regularMarketVolume,shortName,longName";
    private final FxChangeRepository fxChangeRepository;

    public TwelveStockQuoteRepository(FxChangeRepository fxChangeRepository) {
        this.fxChangeRepository = fxChangeRepository;
    }

    private String buildRequestUrl(List<String> symbols) {
        StringBuilder sQuery = new StringBuilder();
        for (String s : symbols) {
            if (!s.equals("")) {
                if (!sQuery.toString().equals("")) {
                    sQuery.append(",");
                }
                sQuery.append(s);
            }
        }
        //return String.format("%s&symbols=%s", BASE_URL, sQuery);
        SharedPreferences preferences = cText.getSharedPreferences(cText.getString(R.string.prefs_name), 0);
        return String.format("%s?symbol=%s&apikey=%s", BASE_URL, sQuery, preferences.getString("twelve_api_key", ""));
    }

    Context cText;

    public HashMap<String, StockQuote> getQuotes(Cache cache, List<String> symbols, Context con) {
        cText = con;
        HashMap<String, StockQuote> quotes = new HashMap<>();
        HashMap<String, String> fxChanges = this.fxChangeRepository.getChanges(cache, symbols);
        JSONArray jsonArray;
        JSONObject quoteJson;

        try {
            jsonArray = this.retrieveQuotesAsJson(cache, symbols);
            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    quoteJson = jsonArray.getJSONObject(i);
                    StockQuote quote = new StockQuote(quoteJson.optString("symbol"), quoteJson.optString("price"), quoteJson.optString("change"), quoteJson.optString("percent"), quoteJson.optString("exchange"), quoteJson.optString("volume"), quoteJson.optString("name"), fxChanges.get(quoteJson.optString("symbol")), Locale.US);
                    quotes.put(quote.getSymbol(), quote);
                }
            }
        } catch (JSONException e) {
            return null;
        }

        return quotes;
    }

    JSONArray retrieveQuotesAsJson(Cache cache, List<String> symbols) throws JSONException {
        String url = buildRequestUrl(symbols);
        String quotesString = UrlDataTools.getCachedUrlData(url, cache, 300);
        // JSONArray quotesJson = new JSONObject(quotesString).getJSONObject("quoteResponse").getJSONArray("result");
        JSONObject quoteJson;
        JSONArray quotes = new JSONArray();

        for (String sym : symbols) {
            quoteJson = new JSONObject(quotesString).getJSONObject(sym); //.optString("close")
            if (quoteJson != null && !quoteJson.optString("status").equalsIgnoreCase("error")) {
                JSONObject data = new JSONObject();
                data.put("symbol", sym);
                data.put("price", quoteJson.optString("close"));
                data.put("change", quoteJson.optString("change"));
                data.put("percent", quoteJson.optString("percent_change"));
                data.put("exchange", quoteJson.optString("exchange"));
                data.put("volume", quoteJson.optString("volume"));
                data.put("name", quoteJson.optString("name"));
                quotes.put(data);
            }
        }

//        for (int i = 0; i < quotesJson.length(); i++) {
//            quoteJson = quotesJson.getJSONObject(i);
//            JSONObject data = new JSONObject();
//            data.put("symbol", quoteJson.optString("symbol"));
//            data.put("price", quoteJson.optString("regularMarketPrice"));
//            data.put("change", quoteJson.optString("regularMarketChange"));
//            data.put("percent", quoteJson.optString("regularMarketChangePercent"));
//            data.put("exchange", quoteJson.optString("fullExchangeName"));
//            data.put("volume", quoteJson.optString("regularMarketVolume"));
//            data.put("name", quoteJson.optString("shortName"));
//            quotes.put(data);
//        }

        return quotes;
    }
}
