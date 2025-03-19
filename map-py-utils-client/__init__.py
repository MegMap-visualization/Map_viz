import os
import sys
from typing import Dict, Optional

from flask import Flask
from flask_cors import CORS
import KcangNacos.nacos as nacos


BASE_DIR = os.path.dirname(__file__)

sys.path.insert(0, BASE_DIR)


def create_app(test_config: Optional[Dict] = None) -> Flask:
    app = Flask(__name__)
    app.url_map.strict_slashes = False

    if test_config is None:
        app.config.from_object("default_settings")
        # load the instance config, if it exists, when not testing
        app.config.from_envvar("MEGMAP_PYTHON_SERVICE_CONFIG", silent=True)
    else:
        # load the test config if passed in
        app.config.from_mapping(test_config)
    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    with app.app_context():
        from .views import (
            coordinate_transformation,
            map_routing_inspector,
            log_extracter_api,
            lane_key_point_extracter_api,
        )  # noqa: E402

    app.register_blueprint(coordinate_transformation.bp)
    app.register_blueprint(map_routing_inspector.bp)
    app.register_blueprint(log_extracter_api.bp)
    app.register_blueprint(lane_key_point_extracter_api.bp)
    CORS(app, supports_credentials=True)

    nacos_server = nacos.nacos(ip=app.config["NACOS_IP"], port=app.config["NACOS_PORT"])

    nacos_server.registerService(
        serviceIp=app.config["INSTANCE_IP"],
        servicePort=app.config["INSTANCE_PORT"],
        serviceName=app.config["INSTANCE_NAME"],
        namespaceId="",
        groupName="",
    )

    return app
