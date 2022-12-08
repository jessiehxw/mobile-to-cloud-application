<%@ page import="com.mongodb.client.*" %>
<%@ page import="com.mongodb.*" %>
<%@ page import="org.bson.Document" %>
<%--
  Created by IntelliJ IDEA.
  User: dell
  Date: 4/9/2022
  Time: 10:55 PM

  Author: Xiawei He
  Andrew-id: xiaweih
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Activity Generator Dashboard</title>
        <%-- import mongodb to embed charts --%>
        <script src="https://unpkg.com/@mongodb-js/charts-embed-dom"></script>
    </head>
    <body>
        <h1 class="title">Activity Generator Dashboard</h1>

        <%-- decorate html --%>
        <style>
            .row {
                margin: auto;
                width: 100%;
                padding: 10px;
            }
            .block {
                /* code referred to
                https://stackoverflow.com/questions/9277311/how-can-i-horizontally-align-my-divs */
                display: inline-block;
                margin: auto;
            }
            .title {
                text-align: center;
                color: #73C2BE;
            }
            table, th, td {
                border: 2px solid #73C2BE;
                border-collapse: collapse;
            }
            th, td {
                padding: 8px 20px;
            }
        </style>


        <%-- code referred to
        https://www.mongodb.com/docs/charts/get-started-embedding-sdk/#std-label-get-started-embedding-sdk
        from MongoDb --%>
        <div class="row">
            <div id="chart1" class="block"></div>
            <iframe style="background: #FFFFFF;
            border: none;
            border-radius: 2px;
            box-shadow: 0 2px 10px 0 rgba(70, 76, 79, .2);"
                    height="480"
                    width="640"
                    src="https://charts.mongodb.com/charts-project-0-fnanr/embed/charts?id=62523d91-3f9e-486f-8c14-13d68a0f6c39&maxDataAge=3600&theme=light&autoRefresh=true"></iframe>
            <script>
                import ChartsEmbedSDK from "@mongodb-js/charts-embed-dom";
                const sdk = new ChartsEmbedSDK({...});
                const chart = sdk.createChart({...});
                chart.render(document.getElementById('chart'));
                const chart1 = sdk.createChart({
                    chartId: '62523d91-3f9e-486f-8c14-13d68a0f6c39'
                });
                chart.render(document.getElementById('chart1'));
            </script>

            <div id="chart2" class="block"></div>
            <iframe style="background: #FFFFFF;
            border: none;
            border-radius: 2px;
            box-shadow: 0 2px 10px 0 rgba(70, 76, 79, .2);"
                    height="480"
                    width="640"
                    src="https://charts.mongodb.com/charts-project-0-fnanr/embed/charts?id=62524073-6131-4a51-816e-3893cfbb8a34&maxDataAge=3600&theme=light&autoRefresh=true"></iframe>
            <script>
                const chart2 = sdk.createChart({
                    chartId: '62524073-6131-4a51-816e-3893cfbb8a34'
                });
                chart.render(document.getElementById('chart2'));
            </script>

            <div id="chart3" class="block"></div>
            <iframe style="background: #FFFFFF;
            border: none;border-radius: 2px;
            box-shadow: 0 2px 10px 0 rgba(70, 76, 79, .2);"
                    height="480"
                    width="640"
                    src="https://charts.mongodb.com/charts-project-0-fnanr/embed/charts?id=625246f0-70f4-4597-8078-68f6a447e9f4&maxDataAge=3600&theme=light&autoRefresh=true"></iframe>
            <script>
                const chart3 = sdk.createChart({
                    chartId: '625246f0-70f4-4597-8078-68f6a447e9f4',
                });
                chart.render(document.getElementById('chart3'));
            </script>
        </div>

        <table>
            <tr>
                <th>id</th>
                <th>Timestamp</th>
                <th>Phone Model</th>
                <th>Search Type</th>
                <th>Mobile Request</th>
                <th>Web Application Request</th>
                <th>Latency (ms)</th>
                <th>API Reply</th>
                <th>Web Application Reply</th>
            </tr>
            <%-- connect to mongodb --%>
            <% ConnectionString connectionString = new ConnectionString("mongodb://xiaweih:Aa13505712920@cluster0-shard-00-00.2dpe2.mongodb.net:27017,cluster0-shard-00-01.2dpe2.mongodb.net:27017,cluster0-shard-00-02.2dpe2.mongodb.net:27017/test?w=majority&retryWrites=true&tls=true&authMechanism=SCRAM-SHA-1");
                MongoClientSettings settings = MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .serverApi(ServerApi.builder()
                                .version(ServerApiVersion.V1)
                                .build())
                        .build();
                MongoClient mongoClient = MongoClients.create(settings);
                MongoDatabase database = mongoClient.getDatabase("ActivityDB");

                MongoCollection collection = database.getCollection("ActivityCollection");
                /* code referred to
                * https://stackoverflow.com/questions/35768418/how-to-use-finditerabledocument-to-get-the-specific-field-from-mongodb-using-j
                * solution by Hughzi
                */
                FindIterable<Document> iterDoc = collection.find();
            %>
            <% for (Document doc: iterDoc) { %>
                <tr>
                    <td><%=doc.get("id")%></td>
                    <td><%=doc.get("Timestamp")%></td>
                    <td><%=doc.get("Phone Model")%></td>
                    <td><%=doc.get("Search Type")%></td>
                    <td><%=doc.get("Mobile Request")%></td>
                    <td><%=doc.get("Web Application Request")%></td>
                    <td><%=doc.get("latency")%></td>
                    <td><%=doc.get("API Reply")%></td>
                    <td><%=doc.get("Web Application Reply")%></td>
                </tr>
            <% } %>
        </table>
    </body>
</html>
