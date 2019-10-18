import Card from "@material-ui/core/Card"
import CardHeader from "@material-ui/core/CardHeader"
import CardMedia from "@material-ui/core/CardMedia"
import CardContent from "@material-ui/core/CardContent"
import React from "react"
import PropTypes from "prop-types"
import { withStyles } from "@material-ui/styles"
import { Typography, Paper, Container, Chip } from "@material-ui/core";
import * as adItemApi from '../../helpers/adItemApi'
import { photoUrl } from "../../helpers/routes"
import Timer from "../../components/Timer"
import CallIcon from '@material-ui/icons/Call';
import NearMe from '@material-ui/icons/NearMe';

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
    id = () => this.props.match.params.id

    state = {
        adItem: null,
        fetched: false,
        phone: ''
    }

    componentDidMount = async () => {
        try {
            const adItem = await adItemApi.getOne(this.id())
            this.setState({ adItem, fetched: true, phone: adItem.phoneObfuscated })
        } catch (e) {
            if (e.status === 404) {
                this.props.history.push(`/notFound/${this.id()}`)
            }
        }
    }

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
                {this.state.fetched ?
                    <Card className={classes.card}>
                        <CardHeader title={adItem.name} subheader={adItem.price + ' ' + adItem.currency}
                            action={<Timer className={classes.timer} validUntil={adItem.validUntil} />} />
                        <CardMedia
                            className={classes.media}
                            image={photoUrl(adItem.photoUrl)}
                            title={adItem.name}
                        />
                        <CardContent>
                            <Container className={classes.details}>
                                <Chip className={classes.itemDetails} variant="outlined" icon={<CallIcon />} onClick={this.showPhone} label={this.state.phone} />
                                <Chip className={classes.itemDetails} variant="outlined" icon={<NearMe />} label={adItem.distanceInKm.toFixed(2)} />
                            </Container>

                            <Typography variant="body2" color="textSecondary" component="p">
                                {adItem.description}
                            </Typography>
                        </CardContent>
                    </Card>
                    : <Paper><Typography variant="body2" color="textSecondary">Loading....</Typography></Paper>
                }
            </Container>
        )
    }
}

AdItemsDetails.propTypes = {
    classes: PropTypes.object.isRequired
}

export default withStyles(styles)(AdItemsDetails)