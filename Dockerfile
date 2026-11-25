FROM eclipse-temurin:21-jdk-jammy AS compilacion
WORKDIR /app
COPY src/ src/
RUN mkdir out && find src -name '*.java' > fuentes.txt \
    && javac -encoding UTF-8 -d out @fuentes.txt

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=compilacion /app/out/ out/
COPY web/ web/
COPY data/ data/
RUN mkdir output && chown 10001:10001 output
USER 10001:10001
ENV HOST=0.0.0.0 PORT=10000
EXPOSE 10000
CMD ["java", "-cp", "out", "Main", "--web"]
