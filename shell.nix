{ pkgs ? import <nixpkgs> {} }:

pkgs.mkShell {
  name = "dentflow-dev";

  buildInputs = with pkgs; [
    # Java / Backend
    jdk21
    maven
    spring-boot-cli

    # Database
    postgresql_15

    # Docker
    docker
    docker-compose

    # Android
    android-tools

    # Additional tools
    git
    curl
    jq
    httpie

    # Documentation / diagrams
    plantuml
    graphviz

    # Editing / formatting
    nodePackages.prettier
  ];

  shellHook = ''
    export JAVA_HOME="${pkgs.jdk21}"
    export MAVEN_OPTS="-Xmx512m"

    echo "  Java:   $(java --version 2>&1 | head -1)"
    echo "  Maven:  $(mvn --version 2>&1 | head -1 | cut -d' ' -f1-3)"
    echo "  psql:   $(psql --version 2>&1 | head -1)"
    echo ""
    echo "  Commands:"
    echo "    cd DentFlow-PZ && mvn compile       # compile backend"
    echo "    docker-compose up postgres          # start database"
    echo "    plantuml -tpng Doc/*.puml           # generate diagrams"
    echo ""
  '';
}
