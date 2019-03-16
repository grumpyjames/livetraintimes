package net.digihippo.ltt.android;

import android.util.Xml;
import net.digihippo.ltt.ldb.AndroidTrainService;
import net.digihippo.ltt.ldb.LdbLiveTrainsService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static net.digihippo.ltt.ldb.AndroidTrainService.readResponse;

@RunWith(RobolectricTestRunner.class)
public class XmlPullingTeethTest
{
    private static final String xmlResponse = "" +
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
        "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"\n" +
        "               xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
        "    <soap:Body>\n" +
        "        <GetDepBoardWithDetailsResponse xmlns=\"http://thalesgroup.com/RTTI/2015-05-14/ldb/\">\n" +
        "            <GetStationBoardResult xmlns:lt=\"http://thalesgroup.com/RTTI/2012-01-13/ldb/types\"\n" +
        "                                   xmlns:lt6=\"http://thalesgroup.com/RTTI/2017-02-02/ldb/types\"\n" +
        "                                   xmlns:lt7=\"http://thalesgroup.com/RTTI/2017-10-01/ldb/types\"\n" +
        "                                   xmlns:lt4=\"http://thalesgroup.com/RTTI/2015-11-27/ldb/types\"\n" +
        "                                   xmlns:lt5=\"http://thalesgroup.com/RTTI/2016-02-16/ldb/types\"\n" +
        "                                   xmlns:lt2=\"http://thalesgroup.com/RTTI/2014-02-20/ldb/types\"\n" +
        "                                   xmlns:lt3=\"http://thalesgroup.com/RTTI/2015-05-14/ldb/types\">\n" +
        "                <lt3:generatedAt>2019-03-14T20:43:09.8171129+00:00</lt3:generatedAt>\n" +
        "                <lt3:locationName>London Victoria</lt3:locationName>\n" +
        "                <lt3:crs>VIC</lt3:crs>\n" +
        "                <lt3:filterLocationName>Gatwick Airport</lt3:filterLocationName>\n" +
        "                <lt3:filtercrs>GTW</lt3:filtercrs>\n" +
        "                <lt3:nrccMessages>\n" +
        "                    <lt:message>There is step free access at London Victoria Rail Station to the Victoria Line on the\n" +
        "                        Underground. This can be accessed via the Cardinal Place entrance to the underground.\n" +
        "                    </lt:message>\n" +
        "                    <lt:message>The toilets on the main concourse and the toilets on platform 2 at London Victoria will\n" +
        "                        be closed for refurbishment until May 2019. Temporary toilets are available outside the station\n" +
        "                        in Hudson's Place off platform 2.\n" +
        "                    </lt:message>\n" +
        "                </lt3:nrccMessages>\n" +
        "                <lt3:platformAvailable>true</lt3:platformAvailable>\n" +
        "                <lt3:trainServices>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>20:44</lt3:std>\n" +
        "                        <lt3:etd>20:49</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>YXGLgvPFXQgwbuqEaTrqiA==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Brighton</lt2:locationName>\n" +
        "                                <lt2:crs>BTN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:11</lt:st>\n" +
        "                                    <lt:et>21:15</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Brighton</lt:locationName>\n" +
        "                                    <lt:crs>BTN</lt:crs>\n" +
        "                                    <lt:st>21:37</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>20:46</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:platform>19</lt3:platform>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>9O6riX/zdtSq9biEBglzFQ==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Hastings</lt2:locationName>\n" +
        "                                <lt2:crs>HGS</lt2:crs>\n" +
        "                                <lt2:via>via Gatwick Airport &amp; Eastbourne</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Littlehampton</lt2:locationName>\n" +
        "                                <lt2:crs>LIT</lt2:crs>\n" +
        "                                <lt2:via>via Hove &amp; Worthing</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>20:52</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:02</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:17</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>21:31</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Plumpton</lt:locationName>\n" +
        "                                    <lt:crs>PMP</lt:crs>\n" +
        "                                    <lt:st>21:45</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Lewes</lt:locationName>\n" +
        "                                    <lt:crs>LWS</lt:crs>\n" +
        "                                    <lt:st>21:53</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Polegate</lt:locationName>\n" +
        "                                    <lt:crs>PLG</lt:crs>\n" +
        "                                    <lt:st>22:06</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Eastbourne</lt:locationName>\n" +
        "                                    <lt:crs>EBN</lt:crs>\n" +
        "                                    <lt:st>22:14</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hampden Park</lt:locationName>\n" +
        "                                    <lt:crs>HMD</lt:crs>\n" +
        "                                    <lt:st>22:24</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Pevensey &amp; Westham</lt:locationName>\n" +
        "                                    <lt:crs>PEV</lt:crs>\n" +
        "                                    <lt:st>22:29</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Cooden Beach</lt:locationName>\n" +
        "                                    <lt:crs>COB</lt:crs>\n" +
        "                                    <lt:st>22:36</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Collington</lt:locationName>\n" +
        "                                    <lt:crs>CLL</lt:crs>\n" +
        "                                    <lt:st>22:39</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Bexhill</lt:locationName>\n" +
        "                                    <lt:crs>BEX</lt:crs>\n" +
        "                                    <lt:st>22:41</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>St Leonards Warrior Square</lt:locationName>\n" +
        "                                    <lt:crs>SLQ</lt:crs>\n" +
        "                                    <lt:st>22:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hastings</lt:locationName>\n" +
        "                                    <lt:crs>HGS</lt:crs>\n" +
        "                                    <lt:st>22:53</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>21:31</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hove</lt:locationName>\n" +
        "                                    <lt:crs>HOV</lt:crs>\n" +
        "                                    <lt:st>21:52</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Portslade</lt:locationName>\n" +
        "                                    <lt:crs>PLD</lt:crs>\n" +
        "                                    <lt:st>21:55</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Southwick</lt:locationName>\n" +
        "                                    <lt:crs>SWK</lt:crs>\n" +
        "                                    <lt:st>21:58</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Shoreham-by-Sea</lt:locationName>\n" +
        "                                    <lt:crs>SSE</lt:crs>\n" +
        "                                    <lt:st>22:01</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Lancing</lt:locationName>\n" +
        "                                    <lt:crs>LAC</lt:crs>\n" +
        "                                    <lt:st>22:06</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Worthing</lt:locationName>\n" +
        "                                    <lt:crs>WRH</lt:crs>\n" +
        "                                    <lt:st>22:10</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>West Worthing</lt:locationName>\n" +
        "                                    <lt:crs>WWO</lt:crs>\n" +
        "                                    <lt:st>22:13</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Durrington-on-Sea</lt:locationName>\n" +
        "                                    <lt:crs>DUR</lt:crs>\n" +
        "                                    <lt:st>22:15</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Goring-by-Sea</lt:locationName>\n" +
        "                                    <lt:crs>GBS</lt:crs>\n" +
        "                                    <lt:st>22:18</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Angmering</lt:locationName>\n" +
        "                                    <lt:crs>ANG</lt:crs>\n" +
        "                                    <lt:st>22:22</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Littlehampton</lt:locationName>\n" +
        "                                    <lt:crs>LIT</lt:crs>\n" +
        "                                    <lt:st>22:32</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>20:55</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>PAqAwRyUAwUN+tWgi0hm2w==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Brighton</lt2:locationName>\n" +
        "                                <lt2:crs>BTN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:01</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:11</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:26</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>21:39</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Burgess Hill</lt:locationName>\n" +
        "                                    <lt:crs>BUG</lt:crs>\n" +
        "                                    <lt:st>21:46</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hassocks</lt:locationName>\n" +
        "                                    <lt:crs>HSK</lt:crs>\n" +
        "                                    <lt:st>21:50</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Brighton</lt:locationName>\n" +
        "                                    <lt:crs>BTN</lt:crs>\n" +
        "                                    <lt:st>22:02</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:00</lt3:std>\n" +
        "                        <lt3:etd>Cancelled</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:filterLocationCancelled>true</lt3:filterLocationCancelled>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>E6UNDXEIAvEexza5+Sq/Lw==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Gatwick Airport</lt2:locationName>\n" +
        "                                <lt2:crs>GTW</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:29</lt:st>\n" +
        "                                    <lt:et>Cancelled</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:06</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>SaU8yKXMBclklG6bfk1ocQ==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Portsmouth Harbour</lt2:locationName>\n" +
        "                                <lt2:crs>PMH</lt2:crs>\n" +
        "                                <lt2:via>via Horsham</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Bognor Regis</lt2:locationName>\n" +
        "                                <lt2:crs>BOG</lt2:crs>\n" +
        "                                <lt2:via>via Horsham</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:12</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:22</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:38</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Three Bridges</lt:locationName>\n" +
        "                                    <lt:crs>TBD</lt:crs>\n" +
        "                                    <lt:st>21:44</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Crawley</lt:locationName>\n" +
        "                                    <lt:crs>CRW</lt:crs>\n" +
        "                                    <lt:st>21:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Horsham</lt:locationName>\n" +
        "                                    <lt:crs>HRH</lt:crs>\n" +
        "                                    <lt:st>21:57</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Barnham</lt:locationName>\n" +
        "                                    <lt:crs>BAA</lt:crs>\n" +
        "                                    <lt:st>22:28</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Chichester</lt:locationName>\n" +
        "                                    <lt:crs>CCH</lt:crs>\n" +
        "                                    <lt:st>22:37</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Southbourne</lt:locationName>\n" +
        "                                    <lt:crs>SOB</lt:crs>\n" +
        "                                    <lt:st>22:45</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Emsworth</lt:locationName>\n" +
        "                                    <lt:crs>EMS</lt:crs>\n" +
        "                                    <lt:st>22:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Havant</lt:locationName>\n" +
        "                                    <lt:crs>HAV</lt:crs>\n" +
        "                                    <lt:st>22:52</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Fratton</lt:locationName>\n" +
        "                                    <lt:crs>FTN</lt:crs>\n" +
        "                                    <lt:st>23:02</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Portsmouth &amp; Southsea</lt:locationName>\n" +
        "                                    <lt:crs>PMS</lt:crs>\n" +
        "                                    <lt:st>23:07</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Portsmouth Harbour</lt:locationName>\n" +
        "                                    <lt:crs>PMH</lt:crs>\n" +
        "                                    <lt:st>23:11</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Horsham</lt:locationName>\n" +
        "                                    <lt:crs>HRH</lt:crs>\n" +
        "                                    <lt:st>21:57</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Christs Hospital</lt:locationName>\n" +
        "                                    <lt:crs>CHH</lt:crs>\n" +
        "                                    <lt:st>22:08</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Billingshurst</lt:locationName>\n" +
        "                                    <lt:crs>BIG</lt:crs>\n" +
        "                                    <lt:st>22:14</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Pulborough</lt:locationName>\n" +
        "                                    <lt:crs>PUL</lt:crs>\n" +
        "                                    <lt:st>22:21</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Amberley</lt:locationName>\n" +
        "                                    <lt:crs>AMY</lt:crs>\n" +
        "                                    <lt:st>22:27</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Arundel</lt:locationName>\n" +
        "                                    <lt:crs>ARU</lt:crs>\n" +
        "                                    <lt:st>22:31</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Ford</lt:locationName>\n" +
        "                                    <lt:crs>FOD</lt:crs>\n" +
        "                                    <lt:st>22:37</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Barnham</lt:locationName>\n" +
        "                                    <lt:crs>BAA</lt:crs>\n" +
        "                                    <lt:st>22:42</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Bognor Regis</lt:locationName>\n" +
        "                                    <lt:crs>BOG</lt:crs>\n" +
        "                                    <lt:st>22:50</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:14</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>OPpurN9kSw++Hc4xznCiHA==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Brighton</lt2:locationName>\n" +
        "                                <lt2:crs>BTN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:41</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Brighton</lt:locationName>\n" +
        "                                    <lt:crs>BTN</lt:crs>\n" +
        "                                    <lt:st>22:07</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:16</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>zGWM/T+3NfV5QnyIS0EmiQ==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Littlehampton</lt2:locationName>\n" +
        "                                <lt2:crs>LIT</lt2:crs>\n" +
        "                                <lt2:via>via Hove &amp; Worthing</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Eastbourne</lt2:locationName>\n" +
        "                                <lt2:crs>EBN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:22</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:32</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:47</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>22:01</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Preston Park</lt:locationName>\n" +
        "                                    <lt:crs>PRP</lt:crs>\n" +
        "                                    <lt:st>22:18</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hove</lt:locationName>\n" +
        "                                    <lt:crs>HOV</lt:crs>\n" +
        "                                    <lt:st>22:22</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Portslade</lt:locationName>\n" +
        "                                    <lt:crs>PLD</lt:crs>\n" +
        "                                    <lt:st>22:25</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Southwick</lt:locationName>\n" +
        "                                    <lt:crs>SWK</lt:crs>\n" +
        "                                    <lt:st>22:28</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Shoreham-by-Sea</lt:locationName>\n" +
        "                                    <lt:crs>SSE</lt:crs>\n" +
        "                                    <lt:st>22:31</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Lancing</lt:locationName>\n" +
        "                                    <lt:crs>LAC</lt:crs>\n" +
        "                                    <lt:st>22:36</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Worthing</lt:locationName>\n" +
        "                                    <lt:crs>WRH</lt:crs>\n" +
        "                                    <lt:st>22:40</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>West Worthing</lt:locationName>\n" +
        "                                    <lt:crs>WWO</lt:crs>\n" +
        "                                    <lt:st>22:43</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Durrington-on-Sea</lt:locationName>\n" +
        "                                    <lt:crs>DUR</lt:crs>\n" +
        "                                    <lt:st>22:45</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Goring-by-Sea</lt:locationName>\n" +
        "                                    <lt:crs>GBS</lt:crs>\n" +
        "                                    <lt:st>22:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Angmering</lt:locationName>\n" +
        "                                    <lt:crs>ANG</lt:crs>\n" +
        "                                    <lt:st>22:52</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Littlehampton</lt:locationName>\n" +
        "                                    <lt:crs>LIT</lt:crs>\n" +
        "                                    <lt:st>23:02</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>22:01</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Wivelsfield</lt:locationName>\n" +
        "                                    <lt:crs>WVF</lt:crs>\n" +
        "                                    <lt:st>22:11</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Cooksbridge</lt:locationName>\n" +
        "                                    <lt:crs>CBR</lt:crs>\n" +
        "                                    <lt:st>22:20</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Lewes</lt:locationName>\n" +
        "                                    <lt:crs>LWS</lt:crs>\n" +
        "                                    <lt:st>22:25</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Polegate</lt:locationName>\n" +
        "                                    <lt:crs>PLG</lt:crs>\n" +
        "                                    <lt:st>22:38</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hampden Park</lt:locationName>\n" +
        "                                    <lt:crs>HMD</lt:crs>\n" +
        "                                    <lt:st>22:43</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Eastbourne</lt:locationName>\n" +
        "                                    <lt:crs>EBN</lt:crs>\n" +
        "                                    <lt:st>22:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:25</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>S3uMnqf0WfTOi7iGlNjQ1g==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Brighton</lt2:locationName>\n" +
        "                                <lt2:crs>BTN</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:31</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:41</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:56</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Haywards Heath</lt:locationName>\n" +
        "                                    <lt:crs>HHE</lt:crs>\n" +
        "                                    <lt:st>22:09</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Burgess Hill</lt:locationName>\n" +
        "                                    <lt:crs>BUG</lt:crs>\n" +
        "                                    <lt:st>22:16</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Hassocks</lt:locationName>\n" +
        "                                    <lt:crs>HSK</lt:crs>\n" +
        "                                    <lt:st>22:20</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Brighton</lt:locationName>\n" +
        "                                    <lt:crs>BTN</lt:crs>\n" +
        "                                    <lt:st>22:32</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:30</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Gatwick Express</lt3:operator>\n" +
        "                        <lt3:operatorCode>GX</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>dLMNm74G1G9UnnNgA76ZVg==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Gatwick Airport</lt2:locationName>\n" +
        "                                <lt2:crs>GTW</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>21:59</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                    <lt3:service>\n" +
        "                        <lt3:std>21:36</lt3:std>\n" +
        "                        <lt3:etd>On time</lt3:etd>\n" +
        "                        <lt3:operator>Southern</lt3:operator>\n" +
        "                        <lt3:operatorCode>SN</lt3:operatorCode>\n" +
        "                        <lt3:serviceType>train</lt3:serviceType>\n" +
        "                        <lt3:serviceID>knzV5Z1jg0upw61EvHbdGg==</lt3:serviceID>\n" +
        "                        <lt3:origin>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>London Victoria</lt2:locationName>\n" +
        "                                <lt2:crs>VIC</lt2:crs>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:origin>\n" +
        "                        <lt3:destination>\n" +
        "                            <lt2:location>\n" +
        "                                <lt2:locationName>Portsmouth &amp; Southsea</lt2:locationName>\n" +
        "                                <lt2:crs>PMS</lt2:crs>\n" +
        "                                <lt2:via>via Horsham</lt2:via>\n" +
        "                            </lt2:location>\n" +
        "                        </lt3:destination>\n" +
        "                        <lt3:previousCallingPoints/>\n" +
        "                        <lt3:subsequentCallingPoints>\n" +
        "                            <lt2:callingPointList serviceType=\"train\" serviceChangeRequired=\"false\"\n" +
        "                                                  assocIsCancelled=\"false\">\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Clapham Junction</lt:locationName>\n" +
        "                                    <lt:crs>CLJ</lt:crs>\n" +
        "                                    <lt:st>21:42</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>East Croydon</lt:locationName>\n" +
        "                                    <lt:crs>ECR</lt:crs>\n" +
        "                                    <lt:st>21:52</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Gatwick Airport</lt:locationName>\n" +
        "                                    <lt:crs>GTW</lt:crs>\n" +
        "                                    <lt:st>22:08</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Three Bridges</lt:locationName>\n" +
        "                                    <lt:crs>TBD</lt:crs>\n" +
        "                                    <lt:st>22:14</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Crawley</lt:locationName>\n" +
        "                                    <lt:crs>CRW</lt:crs>\n" +
        "                                    <lt:st>22:18</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Horsham</lt:locationName>\n" +
        "                                    <lt:crs>HRH</lt:crs>\n" +
        "                                    <lt:st>22:27</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Christs Hospital</lt:locationName>\n" +
        "                                    <lt:crs>CHH</lt:crs>\n" +
        "                                    <lt:st>22:32</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Billingshurst</lt:locationName>\n" +
        "                                    <lt:crs>BIG</lt:crs>\n" +
        "                                    <lt:st>22:38</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Pulborough</lt:locationName>\n" +
        "                                    <lt:crs>PUL</lt:crs>\n" +
        "                                    <lt:st>22:45</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Amberley</lt:locationName>\n" +
        "                                    <lt:crs>AMY</lt:crs>\n" +
        "                                    <lt:st>22:51</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Arundel</lt:locationName>\n" +
        "                                    <lt:crs>ARU</lt:crs>\n" +
        "                                    <lt:st>22:55</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Ford</lt:locationName>\n" +
        "                                    <lt:crs>FOD</lt:crs>\n" +
        "                                    <lt:st>23:02</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Barnham</lt:locationName>\n" +
        "                                    <lt:crs>BAA</lt:crs>\n" +
        "                                    <lt:st>23:06</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Chichester</lt:locationName>\n" +
        "                                    <lt:crs>CCH</lt:crs>\n" +
        "                                    <lt:st>23:15</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Fishbourne (West Sussex)</lt:locationName>\n" +
        "                                    <lt:crs>FSB</lt:crs>\n" +
        "                                    <lt:st>23:18</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Bosham</lt:locationName>\n" +
        "                                    <lt:crs>BOH</lt:crs>\n" +
        "                                    <lt:st>23:21</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Nutbourne</lt:locationName>\n" +
        "                                    <lt:crs>NUT</lt:crs>\n" +
        "                                    <lt:st>23:25</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Southbourne</lt:locationName>\n" +
        "                                    <lt:crs>SOB</lt:crs>\n" +
        "                                    <lt:st>23:27</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Emsworth</lt:locationName>\n" +
        "                                    <lt:crs>EMS</lt:crs>\n" +
        "                                    <lt:st>23:30</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Warblington</lt:locationName>\n" +
        "                                    <lt:crs>WBL</lt:crs>\n" +
        "                                    <lt:st>23:33</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Havant</lt:locationName>\n" +
        "                                    <lt:crs>HAV</lt:crs>\n" +
        "                                    <lt:st>23:36</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Fratton</lt:locationName>\n" +
        "                                    <lt:crs>FTN</lt:crs>\n" +
        "                                    <lt:st>23:48</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                                <lt2:callingPoint>\n" +
        "                                    <lt:locationName>Portsmouth &amp; Southsea</lt:locationName>\n" +
        "                                    <lt:crs>PMS</lt:crs>\n" +
        "                                    <lt:st>23:54</lt:st>\n" +
        "                                    <lt:et>On time</lt:et>\n" +
        "                                </lt2:callingPoint>\n" +
        "                            </lt2:callingPointList>\n" +
        "                        </lt3:subsequentCallingPoints>\n" +
        "                    </lt3:service>\n" +
        "                </lt3:trainServices>\n" +
        "            </GetStationBoardResult>\n" +
        "        </GetDepBoardWithDetailsResponse>\n" +
        "    </soap:Body>\n" +
        "</soap:Envelope>\n";

    @Test
    public void omg() throws IOException, XmlPullParserException
    {
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        parser.setInput(new ByteArrayInputStream(xmlResponse.getBytes("UTF-8")), "UTF-8");
        parser.nextTag();
        System.out.println(readResponse(parser));
    }

    @Test
    @Ignore // handy to capture xml for weird things.
    public void recordRequest() throws IOException, XmlPullParserException
    {
        InputStream inputStream = AndroidTrainService
            .makeBoardRequest(
                "https",
                LdbLiveTrainsService.TOKEN,
                "VIC",
                null
            );
        final byte[] buffer = new byte[2048];
        int read = 1;
        try (final FileOutputStream fileOutputStream = new FileOutputStream("/tmp/response"))
        {
            while (read > 0)
            {
                read = inputStream.read(buffer);
                if (read > 0)
                {
                    fileOutputStream.write(buffer, 0, read);
                }
            }
        }
    }
}
