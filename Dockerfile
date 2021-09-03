FROM openjdk:11
EXPOSE 8080
RUN mkdir ./app
COPY ./gif_currency_service-0.0.1.jar ./app
CMD java -jar ./app/gif_currency_service-0.0.1.jar