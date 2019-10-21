import { photoUrl } from "../../helpers/routes"
import { Typography, Paper, Container, Chip } from "@material-ui/core";
import { withRouter } from 'react-router-dom';
import { withStyles } from "@material-ui/styles"
import * as adItemApi from '../../helpers/adItemApi'
import CallIcon from '@material-ui/icons/Call';
import Card from "@material-ui/core/Card"
import CardContent from "@material-ui/core/CardContent"
import CardHeader from "@material-ui/core/CardHeader"
import CardMedia from "@material-ui/core/CardMedia"
import NearMe from '@material-ui/icons/NearMe';
import PropTypes from "prop-types"
import React from "react"
import Timer from "../../components/Timer"
import Loading from "../../components/Loading";

const styles = theme => ({
    root: {
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center"
    },
    card: {
        width: "85%"
    },
    media: {
        height: 0,
        marginLeft: theme.spacing(4),
        marginRight: theme.spacing(4),
        paddingTop: "56.25%", // 16:9
    },
    details: {
        display: "flex",
        padding: theme.spacing(3),
        justifyContent: "center",
        alignItems: "center"
    },
    itemDetails: {
        marginRight: theme.spacing(2)
    }
})

class AdItemsDetails extends React.Component {
    constructor(props) {
        super(props);
        this._isMounted = false;
    }

    state = {
        adItem: null,
        fetched: false,
        phone: ''
    }

    componentWillUnmount() {
        this._isMounted = false;
    }

    componentDidMount = async () => {
        this._isMounted = true;
        const { lat, long } = this.props

        try {
            const adItem = await adItemApi.getOne(this.id(), lat, long)
            this._isMounted && this.setState({ adItem, fetched: true, phone: adItem.phoneObfuscated })
        } catch (e) {
            console.log(e)
            if (e.status === 404) {
                this.props.history.push(`/notFound/${this.id()}`)
            }
        }
    }

    id = () => this.props.match.params.id

    showPhone = () => {
        if (this.state.phone === this.state.adItem.phone) {
            return
        }

        this.setState({ phone: this.state.adItem.phone })
    };

    render() {
        const { adItem } = this.state
        const { classes } = this.props

        return (
            <Container maxWidth="xl" className={classes.root}>
                {
                    this.state.fetched ?
                        <Card className={classes.card}>
                            <CardHeader title={adItem.name} subheader={adItem.price + ' ' + adItem.currency}
                                action={<Timer validUntil={adItem.validUntilMs} />} />
                            <CardMedia
                                className={classes.media}
                                image={photoUrl(adItem.photoUrl)}
                                title={adItem.name}
                            />
                            <CardContent>
                                <Container className={classes.details}>
                                    <Chip className={classes.itemDetails} variant="outlined" icon={<CallIcon />} onClick={this.showPhone} label={this.state.phone} />
                                    <Chip className={classes.itemDetails} variant="outlined" icon={<NearMe />} label={`${adItem.distanceInKm.toFixed(2)} km`} />
                                </Container>

                                <Typography variant="body2" color="textSecondary" component="p">
                                    {adItem.description}
                                </Typography>
                            </CardContent>
                        </Card>

                        : <Loading />
                }
            </Container>
        )
    }
}

AdItemsDetails.propTypes = {
    classes: PropTypes.object.isRequired
}

export default withRouter(withStyles(styles)(AdItemsDetails))